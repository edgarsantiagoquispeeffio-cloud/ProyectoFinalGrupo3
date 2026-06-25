<?php
require_once 'config.php';

$kpi = db()->query(
    "SELECT
        COUNT(*) AS total_productos,
        COALESCE(SUM(CASE WHEN stock_actual <= stock_minimo THEN 1 ELSE 0 END), 0) AS productos_stock_bajo,
        COALESCE(SUM(stock_actual * precio), 0.00) AS valor_estimado_inventario
     FROM productos"
)->fetch();

$stockBajo = db()->query(
    "SELECT * FROM productos
     WHERE stock_actual <= stock_minimo
     ORDER BY stock_actual ASC, nombre ASC"
)->fetchAll();

$ordenados = db()->query(
    "SELECT * FROM productos
     ORDER BY CASE WHEN stock_actual <= stock_minimo THEN 0 ELSE 1 END,
              stock_actual ASC, nombre ASC"
)->fetchAll();

json_response(true, 'Dashboard obtenido', [
    'kpi' => $kpi,
    'productos_stock_bajo' => $stockBajo,
    'productos_ordenados' => $ordenados
]);
?>
