<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_usuario']);

$idUsuario = as_int($data, 'id_usuario');
$fecha = as_string($data, 'fecha', now_mysql());
$estado = as_string($data, 'estado', 'Completado');

try {
    $pdo = db();
    $pdo->beginTransaction();

    $stmt = $pdo->prepare(
        "SELECT ca.id, ca.id_producto, ca.cantidad, p.precio, p.stock_actual, p.nombre
         FROM carrito ca
         INNER JOIN productos p ON p.id_producto = ca.id_producto
         WHERE ca.id_usuario = ?
         FOR UPDATE"
    );
    $stmt->execute([$idUsuario]);
    $items = $stmt->fetchAll();

    if (empty($items)) {
        $pdo->rollBack();
        json_response(false, 'El carrito está vacío', null, 400);
    }

    $total = 0.0;
    foreach ($items as $item) {
        if (intval($item['stock_actual']) < intval($item['cantidad'])) {
            $pdo->rollBack();
            json_response(false, 'Stock insuficiente para: ' . $item['nombre'], null, 400);
        }
        $total += floatval($item['precio']) * intval($item['cantidad']);
    }

    $stmt = $pdo->prepare('INSERT INTO pedidos (id_usuario, fecha_pedido, total, estado) VALUES (?, ?, ?, ?)');
    $stmt->execute([$idUsuario, $fecha, $total, $estado]);
    $idPedido = intval($pdo->lastInsertId());

    $stmtDetalle = $pdo->prepare(
        'INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario)
         VALUES (?, ?, ?, ?)'
    );
    $stmtStock = $pdo->prepare('UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?');
    $stmtMovimiento = $pdo->prepare(
        'INSERT INTO movimientos_inventario
        (id_producto, tipo, cantidad, stock_anterior, stock_nuevo, motivo, fecha)
        VALUES (?, ?, ?, ?, ?, ?, ?)'
    );

    foreach ($items as $item) {
        $idProducto = intval($item['id_producto']);
        $cantidad = intval($item['cantidad']);
        $precio = floatval($item['precio']);
        $stockAnterior = intval($item['stock_actual']);
        $stockNuevo = $stockAnterior - $cantidad;

        $stmtDetalle->execute([$idPedido, $idProducto, $cantidad, $precio]);
        $stmtStock->execute([$cantidad, $idProducto]);
        $stmtMovimiento->execute([$idProducto, 'SALIDA', $cantidad, $stockAnterior, $stockNuevo, 'Venta / checkout', $fecha]);
    }

    $stmt = $pdo->prepare('DELETE FROM carrito WHERE id_usuario = ?');
    $stmt->execute([$idUsuario]);

    $pdo->commit();

    json_response(true, 'Compra realizada correctamente', [
        'id_pedido' => $idPedido,
        'total' => $total
    ], 201);
} catch (Exception $e) {
    if (db()->inTransaction()) db()->rollBack();
    json_response(false, $e->getMessage(), null, 500);
}
?>
