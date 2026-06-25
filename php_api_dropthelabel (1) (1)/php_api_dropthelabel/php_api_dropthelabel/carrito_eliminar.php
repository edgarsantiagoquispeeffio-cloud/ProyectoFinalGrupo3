<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id']);

$stmt = db()->prepare('DELETE FROM carrito WHERE id = ?');
$stmt->execute([as_int($data, 'id')]);

json_response(true, 'Item eliminado del carrito');
?>
