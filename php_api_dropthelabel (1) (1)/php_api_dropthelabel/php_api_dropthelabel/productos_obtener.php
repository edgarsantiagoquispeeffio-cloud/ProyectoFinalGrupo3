<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_producto']);

$stmt = db()->prepare(
    "SELECT p.*, c.nombre AS categoria, co.nombre AS coleccion
     FROM productos p
     INNER JOIN categorias c ON c.id_categoria = p.id_categoria
     LEFT JOIN colecciones co ON co.id_coleccion = p.id_coleccion
     WHERE p.id_producto = ? LIMIT 1"
);
$stmt->execute([as_int($data, 'id_producto')]);
$producto = $stmt->fetch();

if (!$producto) {
    json_response(false, 'Producto no encontrado', null, 404);
}

json_response(true, 'Producto obtenido', ['producto' => $producto]);
?>
