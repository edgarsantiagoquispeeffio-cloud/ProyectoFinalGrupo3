<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_usuario']);

$stmt = db()->prepare('DELETE FROM carrito WHERE id_usuario = ?');
$stmt->execute([as_int($data, 'id_usuario')]);

json_response(true, 'Carrito limpiado correctamente');
?>
