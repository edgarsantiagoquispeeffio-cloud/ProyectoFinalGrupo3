<?php
require_once 'config.php';
$data = input_data();
require_fields($data, ['id_rol', 'nombre_completo', 'correo', 'contrasena']);

try {
    $stmt = db()->prepare(
        'INSERT INTO usuarios (id_rol, nombre_completo, correo, contrasena)
         VALUES (?, ?, ?, ?)'
    );
    $stmt->execute([
        as_int($data, 'id_rol'),
        as_string($data, 'nombre_completo'),
        as_string($data, 'correo'),
        as_string($data, 'contrasena')
    ]);

    json_response(true, 'Usuario registrado correctamente', [
        'id_usuario' => intval(db()->lastInsertId())
    ], 201);
} catch (PDOException $e) {
    if ($e->getCode() === '23000') {
        json_response(false, 'Ese correo ya está registrado', null, 409);
    }
    json_response(false, $e->getMessage(), null, 500);
}
?>
