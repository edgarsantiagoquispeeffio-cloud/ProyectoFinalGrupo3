<?php
require_once 'config.php';

$sql = "SELECT p.*, c.nombre AS categoria, co.nombre AS coleccion,
        CASE
            WHEN p.stock_actual <= p.stock_minimo THEN 'Stock bajo'
            ELSE 'Stock disponible'
        END AS estado_stock
        FROM productos p
        INNER JOIN categorias c ON c.id_categoria = p.id_categoria
        LEFT JOIN colecciones co ON co.id_coleccion = p.id_coleccion
        ORDER BY p.nombre ASC";

$productos = db()->query($sql)->fetchAll();
json_response(true, 'Productos obtenidos', ['productos' => $productos]);
?>
