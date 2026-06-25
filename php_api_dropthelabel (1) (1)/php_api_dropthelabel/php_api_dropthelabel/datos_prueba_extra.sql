USE app_inventario;

-- Colecciones para que no aparezca vacío en el formulario
INSERT INTO colecciones (id_coleccion, nombre) VALUES
(1, 'Verano 2024'),
(2, 'Edición Limitada')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Productos de prueba para catálogo, carrito, stock y dashboard
INSERT INTO productos
(sku, nombre, id_categoria, id_coleccion, talla, color, precio, stock_actual, stock_minimo) VALUES
('PROD-001', 'Camiseta Basic', 1, 1, 'M', 'Blanco', 19.99, 50, 5),
('PROD-002', 'Zapatillas Runner', 2, NULL, '42', 'Negro', 59.90, 20, 3),
('PROD-003', 'Gorra Sport', 3, 1, 'Única', 'Azul', 15.00, 100, 10),
('PROD-004', 'Pantalón Denim', 1, 2, 'L', 'Gris', 35.50, 15, 2),
('PROD-005', 'Reloj Cuarzo', 3, NULL, 'Única', 'Dorado', 120.00, 8, 1),
('CAM-001', 'Camisa Blanca Slim', 1, 1, 'M', 'Blanco', 59.90, 10, 5),
('ZAP-001', 'Zapatilla Urbana Negra', 2, 2, '42', 'Negro', 129.90, 3, 5)
ON DUPLICATE KEY UPDATE
nombre = VALUES(nombre),
id_categoria = VALUES(id_categoria),
id_coleccion = VALUES(id_coleccion),
talla = VALUES(talla),
color = VALUES(color),
precio = VALUES(precio),
stock_actual = VALUES(stock_actual),
stock_minimo = VALUES(stock_minimo);
