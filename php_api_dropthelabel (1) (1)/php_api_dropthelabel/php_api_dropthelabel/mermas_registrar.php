<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_producto', 'cantidad']);

$idProducto = as_int($data, 'id_producto');
$cantidad = as_int($data, 'cantidad');
$motivo = as_string($data, 'motivo', 'Sin motivo especificado');
$origen = as_string($data, 'origen', 'Merma / baja manual');
$codigoReferencia = as_string($data, 'codigo_referencia', 'MANUAL-' . time());
$fecha = as_string($data, 'fecha', now_mysql());

if ($cantidad <= 0) {
    json_response(false, 'La cantidad debe ser mayor que cero', null, 400);
}

try {
    $pdo = db();
    $pdo->beginTransaction();

    $stmt = $pdo->prepare('SELECT * FROM productos WHERE id_producto = ? FOR UPDATE');
    $stmt->execute([$idProducto]);
    $producto = $stmt->fetch();

    if (!$producto) {
        $pdo->rollBack();
        json_response(false, 'Producto no encontrado', null, 404);
    }

    $stockAnterior = intval($producto['stock_actual']);
    if ($stockAnterior < $cantidad) {
        $pdo->rollBack();
        json_response(false, 'No hay stock suficiente para registrar la merma/baja', null, 400);
    }

    $stockNuevo = $stockAnterior - $cantidad;
    $stmt = $pdo->prepare('UPDATE productos SET stock_actual = ? WHERE id_producto = ?');
    $stmt->execute([$stockNuevo, $idProducto]);

    $stmt = $pdo->prepare(
        'INSERT INTO mermas_bajas
        (id_producto, cantidad, motivo, origen, codigo_referencia, fecha)
        VALUES (?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([$idProducto, $cantidad, $motivo, $origen, $codigoReferencia, $fecha]);
    $idMerma = intval($pdo->lastInsertId());

    $stmt = $pdo->prepare(
        'INSERT INTO movimientos_inventario
        (id_producto, tipo, cantidad, stock_anterior, stock_nuevo, motivo, fecha)
        VALUES (?, ?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([$idProducto, 'MERMA', $cantidad, $stockAnterior, $stockNuevo, $motivo, $fecha]);

    $pdo->commit();

    json_response(true, 'Merma/baja registrada correctamente', [
        'id_merma' => $idMerma,
        'stock_anterior' => $stockAnterior,
        'stock_nuevo' => $stockNuevo
    ], 201);
} catch (Exception $e) {
    if (db()->inTransaction()) db()->rollBack();
    json_response(false, $e->getMessage(), null, 500);
}
?>
