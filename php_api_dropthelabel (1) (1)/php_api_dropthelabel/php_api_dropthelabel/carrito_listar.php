<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_usuario']);

$idUsuario = as_int($data, 'id_usuario');
$sql = "SELECT ca.id, ca.id_usuario, ca.id_producto, ca.cantidad,
               p.sku, p.nombre, p.talla, p.color, p.precio, p.stock_actual,
               (ca.cantidad * p.precio) AS subtotal
        FROM carrito ca
        INNER JOIN productos p ON p.id_producto = ca.id_producto
        WHERE ca.id_usuario = ?
        ORDER BY ca.id DESC";
$stmt = db()->prepare($sql);
$stmt->execute([$idUsuario]);
$items = $stmt->fetchAll();

$total = 0.0;
foreach ($items as $item) {
    $total += floatval($item['subtotal']);
}

json_response(true, 'Carrito obtenido', [
    'items' => $items,
    'total' => $total
]);
?>
