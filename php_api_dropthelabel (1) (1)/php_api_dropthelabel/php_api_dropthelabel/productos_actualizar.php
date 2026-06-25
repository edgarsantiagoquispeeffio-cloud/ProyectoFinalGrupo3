<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_producto']);

$idProducto = as_int($data, 'id_producto');
$stmt = db()->prepare('SELECT * FROM productos WHERE id_producto = ? LIMIT 1');
$stmt->execute([$idProducto]);
$actual = $stmt->fetch();

if (!$actual) {
    json_response(false, 'Producto no encontrado', null, 404);
}

$idColeccion = array_key_exists('id_coleccion', $data)
    ? ($data['id_coleccion'] === '' ? null : intval($data['id_coleccion']))
    : $actual['id_coleccion'];

try {
    $stmt = db()->prepare(
        'UPDATE productos SET
            sku = ?, nombre = ?, id_categoria = ?, id_coleccion = ?, talla = ?, color = ?,
            precio = ?, stock_actual = ?, stock_minimo = ?
         WHERE id_producto = ?'
    );
    $stmt->execute([
        as_string($data, 'sku', $actual['sku']),
        as_string($data, 'nombre', $actual['nombre']),
        as_int($data, 'id_categoria', intval($actual['id_categoria'])),
        $idColeccion,
        as_string($data, 'talla', $actual['talla']),
        as_string($data, 'color', $actual['color']),
        as_float($data, 'precio', floatval($actual['precio'])),
        as_int($data, 'stock_actual', intval($actual['stock_actual'])),
        as_int($data, 'stock_minimo', intval($actual['stock_minimo'])),
        $idProducto
    ]);

    json_response(true, 'Producto actualizado correctamente');
} catch (PDOException $e) {
    json_response(false, $e->getMessage(), null, 500);
}
?>
