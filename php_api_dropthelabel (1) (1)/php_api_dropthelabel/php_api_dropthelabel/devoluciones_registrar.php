<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['codigo_venta', 'id_producto', 'cantidad', 'estado_producto']);

$codigoVenta = as_string($data, 'codigo_venta');
$idProducto = as_int($data, 'id_producto');
$cantidad = as_int($data, 'cantidad');
$motivo = as_string($data, 'motivo', 'Sin motivo especificado');
$estadoProducto = as_string($data, 'estado_producto');
$fecha = as_string($data, 'fecha', now_mysql());

if ($cantidad <= 0) {
    json_response(false, 'La cantidad a devolver debe ser mayor que cero', null, 400);
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
    $estadoLower = mb_strtolower($estadoProducto, 'UTF-8');

    if (isset($data['reintegrado_stock'])) {
        $reintegrar = intval($data['reintegrado_stock']) === 1 || $data['reintegrado_stock'] === true || $data['reintegrado_stock'] === 'true';
    } else {
        $reintegrar = str_contains($estadoLower, 'buen') || str_contains($estadoLower, 'apto') || str_contains($estadoLower, 'nuevo') || str_contains($estadoLower, 'util') || str_contains($estadoLower, 'útil');
    }

    $stockNuevo = $reintegrar ? ($stockAnterior + $cantidad) : $stockAnterior;

    if ($reintegrar) {
        $stmt = $pdo->prepare('UPDATE productos SET stock_actual = ? WHERE id_producto = ?');
        $stmt->execute([$stockNuevo, $idProducto]);

        $stmt = $pdo->prepare(
            'INSERT INTO movimientos_inventario
            (id_producto, tipo, cantidad, stock_anterior, stock_nuevo, motivo, fecha)
            VALUES (?, ?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $idProducto,
            'DEVOLUCION',
            $cantidad,
            $stockAnterior,
            $stockNuevo,
            'Devolución útil: ' . $motivo,
            $fecha
        ]);
    } else {
        $stmt = $pdo->prepare(
            'INSERT INTO mermas_bajas
            (id_producto, cantidad, motivo, origen, codigo_referencia, fecha)
            VALUES (?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $idProducto,
            $cantidad,
            $motivo,
            'Devolución dañada / defectuosa',
            $codigoVenta,
            $fecha
        ]);
    }

    $stmt = $pdo->prepare(
        'INSERT INTO devoluciones
        (codigo_venta, id_producto, cantidad, motivo, estado_producto, reintegrado_stock, stock_anterior, stock_nuevo, fecha)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([
        $codigoVenta,
        $idProducto,
        $cantidad,
        $motivo,
        $estadoProducto,
        $reintegrar ? 1 : 0,
        $stockAnterior,
        $stockNuevo,
        $fecha
    ]);

    $idDevolucion = intval($pdo->lastInsertId());
    $pdo->commit();

    json_response(true, 'Devolución registrada correctamente', [
        'id_devolucion' => $idDevolucion,
        'reintegrado_stock' => $reintegrar,
        'stock_anterior' => $stockAnterior,
        'stock_nuevo' => $stockNuevo
    ], 201);
} catch (Exception $e) {
    if (db()->inTransaction()) db()->rollBack();
    json_response(false, $e->getMessage(), null, 500);
}
?>
