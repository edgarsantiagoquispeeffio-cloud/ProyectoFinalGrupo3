<?php
require_once 'config.php';

$roles = db()->query('SELECT * FROM roles ORDER BY id_rol')->fetchAll();
$categorias = db()->query('SELECT * FROM categorias ORDER BY nombre')->fetchAll();
$colecciones = db()->query('SELECT * FROM colecciones ORDER BY nombre')->fetchAll();

json_response(true, 'Catálogos obtenidos', [
    'roles' => $roles,
    'categorias' => $categorias,
    'colecciones' => $colecciones
]);
?>
