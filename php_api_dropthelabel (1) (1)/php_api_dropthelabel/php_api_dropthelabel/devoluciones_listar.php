<?php
require_once 'config.php';

$sql = "SELECT d.*, p.sku, p.nombre AS producto
        FROM devoluciones d
        INNER JOIN productos p ON p.id_producto = d.id_producto
        ORDER BY d.id_devolucion DESC";
$devoluciones = db()->query($sql)->fetchAll();
json_response(true, 'Devoluciones obtenidas', ['devoluciones' => $devoluciones]);
?>
