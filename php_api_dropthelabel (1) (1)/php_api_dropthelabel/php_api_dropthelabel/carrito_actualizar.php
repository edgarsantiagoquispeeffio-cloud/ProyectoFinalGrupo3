<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id', 'cantidad']);

$id = as_int($data, 'id');
$cantidad = as_int($data, 'cantidad');

if ($cantidad <= 0) {
    json_response(false, 'La cantidad debe ser mayor que cero', null, 400);
}

$stmt = db()->prepare('UPDATE carrito SET cantidad = ? WHERE id = ?');
$stmt->execute([$cantidad, $id]);

if ($stmt->rowCount() === 0) {
    json_response(false, 'Item de carrito no encontrado o sin cambios', null, 404);
}

json_response(true, 'Cantidad actualizada correctamente');
?>
