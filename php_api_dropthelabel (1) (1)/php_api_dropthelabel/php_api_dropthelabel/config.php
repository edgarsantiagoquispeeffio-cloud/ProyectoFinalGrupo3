<?php
// CONFIGURACIÓN GENERAL API - XAMPP + MySQL Workbench
// Carpeta sugerida: C:\xampp\htdocs\dropthelabel_api\

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

// En XAMPP normalmente el usuario es root y la contraseña está vacía.
$DB_HOST = 'localhost';
$DB_NAME = 'app_inventario'; // Si tu BD tiene otro nombre, cámbialo aquí.
$DB_USER = 'root';
$DB_PASS = '';

function db() {
    static $pdo = null;
    global $DB_HOST, $DB_NAME, $DB_USER, $DB_PASS;

    if ($pdo === null) {
        try {
            $pdo = new PDO(
                "mysql:host={$DB_HOST};dbname={$DB_NAME};charset=utf8mb4",
                $DB_USER,
                $DB_PASS,
                [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false
                ]
            );
        } catch (PDOException $e) {
            json_response(false, 'Error de conexión a MySQL: ' . $e->getMessage(), null, 500);
        }
    }
    return $pdo;
}

function input_data() {
    $raw = file_get_contents('php://input');
    $json = json_decode($raw, true);
    if (is_array($json)) {
        return array_merge($_GET, $_POST, $json);
    }
    return array_merge($_GET, $_POST);
}

function json_response($success, $message = '', $data = null, $code = 200) {
    http_response_code($code);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}

function require_fields($data, $fields) {
    foreach ($fields as $field) {
        if (!isset($data[$field]) || $data[$field] === '') {
            json_response(false, "Falta el campo: {$field}", null, 400);
        }
    }
}

function as_int($data, $key, $default = 0) {
    return isset($data[$key]) && $data[$key] !== '' ? intval($data[$key]) : $default;
}

function as_float($data, $key, $default = 0.0) {
    return isset($data[$key]) && $data[$key] !== '' ? floatval($data[$key]) : $default;
}

function as_string($data, $key, $default = '') {
    return isset($data[$key]) ? trim(strval($data[$key])) : $default;
}

function now_mysql() {
    return date('Y-m-d H:i:s');
}
?>
