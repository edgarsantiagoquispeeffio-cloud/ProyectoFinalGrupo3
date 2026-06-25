<?php
require_once 'config.php';

$sql = "SELECT m.*, p.sku, p.nombre AS producto
        FROM mermas_bajas m
        INNER JOIN productos p ON p.id_producto = m.id_producto
        ORDER BY m.id_merma DESC";
$mermas = db()->query($sql)->fetchAll();
json_response(true, 'Mermas/bajas obtenidas', ['mermas' => $mermas]);
?>
