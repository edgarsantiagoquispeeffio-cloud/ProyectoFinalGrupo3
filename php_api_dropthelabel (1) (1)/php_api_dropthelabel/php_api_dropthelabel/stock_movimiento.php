<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_producto', 'tipo', 'cantidad']);

$idProducto = as_int($data, 'id_producto');
$tipo = strtoupper(as_string($data, 'tipo'));
$cantidad = as_int($data, 'cantidad');
$motivo = as_string($data, 'motivo', 'Sin motivo especificado');
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

    if ($tipo === 'ENTRADA') {
        $stockNuevo = $stockAnterior + $cantidad;
    } elseif ($tipo === 'SALIDA') {
        if ($stockAnterior < $cantidad) {
            $pdo->rollBack();
            json_response(false, 'No se permite una salida sin stock disponible', null, 400);
        }
        $stockNuevo = $stockAnterior - $cantidad;
    } else {
        $pdo->rollBack();
        json_response(false, 'Tipo inválido. Usa ENTRADA o SALIDA', null, 400);
    }

    $stmt = $pdo->prepare('UPDATE productos SET stock_actual = ? WHERE id_producto = ?');
    $stmt->execute([$stockNuevo, $idProducto]);

    $stmt = $pdo->prepare(
        'INSERT INTO movimientos_inventario
        (id_producto, tipo, cantidad, stock_anterior, stock_nuevo, motivo, fecha)
        VALUES (?, ?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([$idProducto, $tipo, $cantidad, $stockAnterior, $stockNuevo, $motivo, $fecha]);

    $idMovimiento = intval($pdo->lastInsertId());
    $pdo->commit();

    json_response(true, 'Movimiento registrado correctamente', [
        'id_movimiento' => $idMovimiento,
        'stock_anterior' => $stockAnterior,
        'stock_nuevo' => $stockNuevo
    ], 201);
} catch (Exception $e) {
    if (db()->inTransaction()) db()->rollBack();
    json_response(false, $e->getMessage(), null, 500);
}
?>
