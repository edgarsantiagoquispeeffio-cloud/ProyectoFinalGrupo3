package com.example.proyectofinalgrupo3.data.dao

import androidx.room.*
import com.example.proyectofinalgrupo3.data.entities.*

@Dao
interface EcommerceDao {

    // Roles
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRol(rol: RolEntity)

    @Query("SELECT * FROM roles")
    suspend fun getAllRoles(): List<RolEntity>

    // Usuarios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioByEmail(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<UsuarioEntity>

    // Categorias
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias")
    suspend fun getAllCategorias(): List<CategoriaEntity>

    // Colecciones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColeccion(coleccion: ColeccionEntity)

    // Productos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos")
    suspend fun getAllProductos(): List<ProductoEntity>

    @Query("UPDATE productos SET stock_actual = stock_actual - :cantidad WHERE id_producto = :idProducto")
    suspend fun reduceStock(idProducto: Int, cantidad: Int)

    @Query("SELECT * FROM productos WHERE id_producto = :idProducto LIMIT 1")
    suspend fun getProductoById(idProducto: Int): ProductoEntity?

    // Carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarrito(carrito: CarritoEntity)

    @Query("SELECT * FROM carrito WHERE id_usuario = :idUsuario")
    suspend fun getCarritoByUsuario(idUsuario: Int): List<CarritoEntity>

    @Delete
    suspend fun deleteCarritoItem(carrito: CarritoEntity)

    @Query("DELETE FROM carrito WHERE id_usuario = :idUsuario")
    suspend fun clearCarrito(idUsuario: Int)

    // Pedidos y Detalles
    @Insert
    suspend fun insertPedido(pedido: PedidoEntity): Long

    @Insert
    suspend fun insertDetallePedido(detalle: DetallePedidoEntity)

    @Query("SELECT * FROM pedidos ORDER BY fecha_pedido DESC")
    suspend fun getAllPedidos(): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE id_usuario = :idUsuario ORDER BY fecha_pedido DESC")
    suspend fun getPedidosByUsuario(idUsuario: Int): List<PedidoEntity>

    @Query("SELECT * FROM carrito WHERE id_usuario = :idUsuario AND id_producto = :idProducto LIMIT 1")
    suspend fun getCartItem(idUsuario: Int, idProducto: Int): CarritoEntity?

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :id")
    suspend fun updateCarritoCantidad(id: Int, cantidad: Int)

    @Transaction
    suspend fun addOrUpdateCart(idUsuario: Int, idProducto: Int, cantidad: Int) {
        val existing = getCartItem(idUsuario, idProducto)
        if (existing != null) {
            updateCarritoCantidad(existing.id, existing.cantidad + cantidad)
        } else {
            insertCarrito(CarritoEntity(idUsuario = idUsuario, idProducto = idProducto, cantidad = cantidad))
        }
    }

    @Query("SELECT nombre FROM categorias WHERE id_categoria = :id LIMIT 1")
    suspend fun getNombreCategoria(id: Int): String?

    @Query("SELECT nombre FROM colecciones WHERE id_coleccion = :id LIMIT 1")
    suspend fun getNombreColeccion(id: Int): String?

    @Transaction
    suspend fun realizarCompra(idUsuario: Int, fecha: String) {
        val itemsCarrito = getCarritoByUsuario(idUsuario)
        if (itemsCarrito.isEmpty()) return

        // 1. Calcular total y crear el pedido
        var total = 0.0
        val detalles = mutableListOf<Pair<CarritoEntity, Double>>()
        
        for (item in itemsCarrito) {
            val producto = getProductoById(item.idProducto)
            if (producto != null) {
                total += producto.precio * item.cantidad
                detalles.add(item to producto.precio)
            }
        }

        val pedidoId = insertPedido(
            PedidoEntity(
                idUsuario = idUsuario,
                fechaPedido = fecha,
                total = total,
                estado = "Completado"
            )
        ).toInt()

        // 2. Insertar detalles y reducir stock
        for ((item, precio) in detalles) {
            insertDetallePedido(
                DetallePedidoEntity(
                    idPedido = pedidoId,
                    idProducto = item.idProducto,
                    cantidad = item.cantidad,
                    precioUnitario = precio
                )
            )
            reduceStock(item.idProducto, item.cantidad)
        }

        // 3. Limpiar carrito
        clearCarrito(idUsuario)
    }
}
