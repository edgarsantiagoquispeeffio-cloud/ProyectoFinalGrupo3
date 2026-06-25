package com.example.proyectofinalgrupo3.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinalgrupo3.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseCallback(
    private val scope: CoroutineScope,
    private val dbProvider: () -> AppDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch(Dispatchers.IO) {
            populateDatabase()
        }
    }

    private suspend fun populateDatabase() {
        val database = dbProvider()
        val catalogosDao = database.catalogosBasicosDao()
        val usuarioDao = database.usuarioDao()
        val productoDao = database.productoDao()

        // 1. Roles
        val roles = listOf(
            RolEntity(idRol = 1, nombre = "Administrador"),
            RolEntity(idRol = 2, nombre = "Vendedor"),
            RolEntity(idRol = 3, nombre = "Cliente")
        )
        catalogosDao.insertRoles(roles)

        // 2. Usuarios
        val usuarios = listOf(
            UsuarioEntity(
                idUsuario = 1,
                idRol = 1,
                nombreCompleto = "Admin Sistema",
                correo = "admin@ecommerce.com",
                contrasena = "pass123"
            ),
            UsuarioEntity(
                idUsuario = 2,
                idRol = 3,
                nombreCompleto = "Juan Perez",
                correo = "juan@gmail.com",
                contrasena = "pass123"
            )
        )
        usuarioDao.insertUsuarios(usuarios)

        // 3. Categorías
        val categorias = listOf(
            CategoriaEntity(idCategoria = 1, nombre = "Ropa"),
            CategoriaEntity(idCategoria = 2, nombre = "Calzado"),
            CategoriaEntity(idCategoria = 3, nombre = "Accesorios")
        )
        catalogosDao.insertCategorias(categorias)

        // 4. Colecciones
        val colecciones = listOf(
            ColeccionEntity(idColeccion = 1, nombre = "Verano 2024"),
            ColeccionEntity(idColeccion = 2, nombre = "Edición Limitada")
        )
        catalogosDao.insertColecciones(colecciones)

        // 5. Productos
        val productos = listOf(
            ProductoEntity(
                sku = "PROD-001", nombre = "Camiseta Basic", idCategoria = 1, idColeccion = 1,
                talla = "M", color = "Blanco", precio = 19.99, stockActual = 50, stockMinimo = 5
            ),
            ProductoEntity(
                sku = "PROD-002", nombre = "Zapatillas Runner", idCategoria = 2, idColeccion = null,
                talla = "42", color = "Negro", precio = 59.90, stockActual = 20, stockMinimo = 3
            ),
            ProductoEntity(
                sku = "PROD-003", nombre = "Gorra Sport", idCategoria = 3, idColeccion = 1,
                talla = "Única", color = "Azul", precio = 15.00, stockActual = 100, stockMinimo = 10
            ),
            ProductoEntity(
                sku = "PROD-004", nombre = "Pantalón Denim", idCategoria = 1, idColeccion = 2,
                talla = "L", color = "Gris", precio = 35.50, stockActual = 15, stockMinimo = 2
            ),
            ProductoEntity(
                sku = "PROD-005", nombre = "Reloj Cuarzo", idCategoria = 3, idColeccion = null,
                talla = "Única", color = "Dorado", precio = 120.00, stockActual = 8, stockMinimo = 1
            )
        )
        productoDao.insertarProductos(productos)
    }
}
