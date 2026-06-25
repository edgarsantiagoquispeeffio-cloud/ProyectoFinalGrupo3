<?php
require_once 'config.php';
try {
    $stmt = db()->query('SELECT DATABASE() AS base_datos, NOW() AS fecha_servidor');
    json_response(true, 'Conexión correcta', $stmt->fetch());
} catch (Exception $e) {
    json_response(false, $e->getMessage(), null, 500);
}
?>
