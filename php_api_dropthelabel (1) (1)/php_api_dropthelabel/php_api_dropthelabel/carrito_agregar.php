<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_usuario', 'id_producto', 'cantidad']);

$idUsuario = as_int($data, 'id_usuario');
$idProducto = as_int($data, 'id_producto');
$cantidad = as_int($data, 'cantidad');

if ($cantidad <= 0) {
    json_response(false, 'La cantidad debe ser mayor que cero', null, 400);
}

try {
    $pdo = db();

    $stmt = $pdo->prepare('SELECT id FROM carrito WHERE id_usuario = ? AND id_producto = ? LIMIT 1');
    $stmt->execute([$idUsuario, $idProducto]);
    $item = $stmt->fetch();

    if ($item) {
        $stmt = $pdo->prepare('UPDATE carrito SET cantidad = cantidad + ? WHERE id = ?');
        $stmt->execute([$cantidad, intval($item['id'])]);
        $idCarrito = intval($item['id']);
        $mensaje = 'Producto actualizado en el carrito';
    } else {
        $stmt = $pdo->prepare('INSERT INTO carrito (id_usuario, id_producto, cantidad) VALUES (?, ?, ?)');
        $stmt->execute([$idUsuario, $idProducto, $cantidad]);
        $idCarrito = intval($pdo->lastInsertId());
        $mensaje = 'Producto agregado al carrito';
    }

    json_response(true, $mensaje, ['id' => $idCarrito], 201);
} catch (PDOException $e) {
    if ($e->getCode() === '23000') {
        json_response(false, 'Usuario o producto inválido', null, 409);
    }
    json_response(false, $e->getMessage(), null, 500);
}
?>
