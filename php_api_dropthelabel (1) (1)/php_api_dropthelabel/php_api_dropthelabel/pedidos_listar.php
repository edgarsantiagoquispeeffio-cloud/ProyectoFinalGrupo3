<?php
require_once 'config.php';
$data = input_data();
$idUsuario = isset($data['id_usuario']) && $data['id_usuario'] !== '' ? intval($data['id_usuario']) : null;

if ($idUsuario) {
    $stmt = db()->prepare(
        "SELECT p.*, u.nombre_completo, u.correo
         FROM pedidos p
         INNER JOIN usuarios u ON u.id_usuario = p.id_usuario
         WHERE p.id_usuario = ?
         ORDER BY p.fecha_pedido DESC"
    );
    $stmt->execute([$idUsuario]);
} else {
    $stmt = db()->query(
        "SELECT p.*, u.nombre_completo, u.correo
         FROM pedidos p
         INNER JOIN usuarios u ON u.id_usuario = p.id_usuario
         ORDER BY p.fecha_pedido DESC"
    );
}

$pedidos = $stmt->fetchAll();

$stmtDetalle = db()->prepare(
    "SELECT d.*, pr.sku, pr.nombre AS producto
     FROM detalle_pedidos d
     INNER JOIN productos pr ON pr.id_producto = d.id_producto
     WHERE d.id_pedido = ?"
);

foreach ($pedidos as &$pedido) {
    $stmtDetalle->execute([intval($pedido['id_pedido'])]);
    $pedido['detalles'] = $stmtDetalle->fetchAll();
}

json_response(true, 'Pedidos obtenidos', ['pedidos' => $pedidos]);
?>
