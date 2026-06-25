PHP API PARA DROP THE LABEL / APP INVENTARIO

1) Copia la carpeta completa en:
   C:\xampp\htdocs\dropthelabel_api\

2) En MySQL Workbench ya debes tener creada la BD con el script DropTheLabelSql.sql.
   La BD debe llamarse: app_inventario
   Si tu BD tiene otro nombre, abre config.php y cambia esta línea:
   $DB_NAME = 'app_inventario';

3) Opcional pero recomendado:
   Ejecuta datos_prueba_extra.sql en MySQL Workbench para tener colecciones y productos de prueba.

4) Prueba en el navegador:
   http://localhost/dropthelabel_api/prueba_conexion.php
   http://localhost/dropthelabel_api/productos_listar.php

5) Prueba login:
   URL: http://localhost/dropthelabel_api/login.php
   Método: POST
   Body JSON:
   {
     "correo": "admin@ecommerce.com",
     "contrasena": "pass123"
   }

6) OJO IMPORTANTE PARA ANDROID:
   Si usas emulador Android Studio, la URL no es localhost.
   Usa:
   http://10.0.2.2/dropthelabel_api/

   Si usas celular físico conectado a la misma red WiFi, usa la IP de tu PC:
   http://IP_DE_TU_PC/dropthelabel_api/

7) Permisos Android necesarios si conectas la app a PHP:
   En AndroidManifest.xml agrega:
   <uses-permission android:name="android.permission.INTERNET" />

   Y si usas HTTP sin SSL, agrega dentro de <application>:
   android:usesCleartextTraffic="true"

ARCHIVOS INCLUIDOS:
- config.php
- prueba_conexion.php
- login.php
- usuarios_registrar.php
- catalogos_listar.php
- productos_listar.php
- productos_obtener.php
- productos_registrar.php
- productos_actualizar.php
- dashboard_stock.php
- stock_movimiento.php
- movimientos_listar.php
- devoluciones_registrar.php
- devoluciones_listar.php
- mermas_registrar.php
- mermas_listar.php
- carrito_agregar.php
- carrito_listar.php
- carrito_actualizar.php
- carrito_eliminar.php
- carrito_limpiar.php
- checkout.php
- pedidos_listar.php
- datos_prueba_extra.sql
