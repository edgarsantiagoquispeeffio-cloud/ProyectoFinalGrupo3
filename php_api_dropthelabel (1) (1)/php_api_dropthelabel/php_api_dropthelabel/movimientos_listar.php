<?php
require_once 'config.php';

$sql = "SELECT m.*, p.sku, p.nombre AS producto
        FROM movimientos_inventario m
        INNER JOIN productos p ON p.id_producto = m.id_producto
        ORDER BY m.id_movimiento DESC";

$movimientos = db()->query($sql)->fetchAll();
json_response(true, 'Movimientos obtenidos', ['movimientos' => $movimientos]);
?>
