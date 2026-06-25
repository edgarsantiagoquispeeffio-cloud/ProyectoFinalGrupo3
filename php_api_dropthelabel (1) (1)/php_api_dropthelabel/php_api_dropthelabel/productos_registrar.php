<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['sku', 'nombre', 'id_categoria', 'precio', 'stock_actual', 'stock_minimo']);

try {
    $idColeccion = isset($data['id_coleccion']) && $data['id_coleccion'] !== '' ? intval($data['id_coleccion']) : null;

    $stmt = db()->prepare(
        'INSERT INTO productos
        (sku, nombre, id_categoria, id_coleccion, talla, color, precio, stock_actual, stock_minimo)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)'
    );
    $stmt->execute([
        as_string($data, 'sku'),
        as_string($data, 'nombre'),
        as_int($data, 'id_categoria'),
        $idColeccion,
        as_string($data, 'talla', 'Única'),
        as_string($data, 'color', ''),
        as_float($data, 'precio'),
        as_int($data, 'stock_actual'),
        as_int($data, 'stock_minimo')
    ]);

    json_response(true, 'Producto registrado correctamente', [
        'id_producto' => intval(db()->lastInsertId())
    ], 201);
} catch (PDOException $e) {
    if ($e->getCode() === '23000') {
        json_response(false, 'El SKU ya existe o hay una categoría/colección inválida', null, 409);
    }
    json_response(false, $e->getMessage(), null, 500);
}
?>
