<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['correo', 'contrasena']);

$correo = as_string($data, 'correo');
$contrasena = as_string($data, 'contrasena');

$stmt = db()->prepare(
    'SELECT u.id_usuario, u.id_rol, r.nombre AS rol, u.nombre_completo, u.correo
     FROM usuarios u
     INNER JOIN roles r ON r.id_rol = u.id_rol
     WHERE u.correo = ? AND u.contrasena = ?
     LIMIT 1'
);
$stmt->execute([$correo, $contrasena]);
$usuario = $stmt->fetch();

if (!$usuario) {
    json_response(false, 'Correo o contraseña incorrectos', null, 401);
}

json_response(true, 'Login correcto', ['usuario' => $usuario]);
?>
