package com.example.proyectofinalgrupo3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.proyectofinalgrupo3.data.business.InventarioBusinessRules
import com.example.proyectofinalgrupo3.data.entities.DevolucionEntity
import com.example.proyectofinalgrupo3.data.entities.MermaEntity
import com.example.proyectofinalgrupo3.data.entities.MovimientoInventarioEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.data.model.InventarioKpi
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerCatalogo(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos WHERE id_producto = :idProducto LIMIT 1")
    suspend fun obtenerProductoPorId(idProducto: Int): ProductoEntity?

    @Query("UPDATE productos SET stock_actual = :nuevoStock WHERE id_producto = :idProducto")
    suspend fun actualizarStockProducto(idProducto: Int, nuevoStock: Int)

    @Query("UPDATE productos SET stock_actual = stock_actual + :cantidad WHERE id_producto = :idProducto")
    suspend fun incrementarStockAtomicamente(idProducto: Int, cantidad: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMovimientoInventario(movimiento: MovimientoInventarioEntity)

    @Query("SELECT * FROM movimientos_inventario ORDER BY id_movimiento DESC")
    fun obtenerMovimientosInventario(): Flow<List<MovimientoInventarioEntity>>

    // HU-004: devoluciones y mermas/bajas.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDevolucion(devolucion: DevolucionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMerma(merma: MermaEntity)

    @Query("SELECT * FROM devoluciones ORDER BY id_devolucion DESC")
    fun observarDevoluciones(): Flow<List<DevolucionEntity>>

    @Query("SELECT * FROM mermas_bajas ORDER BY id_merma DESC")
    fun observarMermas(): Flow<List<MermaEntity>>

    // HU-005: indicadores reactivos y livianos para dashboard.
    @Query(
        """
        SELECT
            COUNT(*) AS totalProductos,
            COALESCE(SUM(CASE WHEN stock_actual < 5 THEN 1 ELSE 0 END), 0) AS productosStockBajo,
            COALESCE(SUM(stock_actual * precio), 0.0) AS valorEstimadoInventario
        FROM productos
        """
    )
    fun observarIndicadoresInventario(): Flow<InventarioKpi>

    @Query("SELECT * FROM productos WHERE stock_actual < 5 ORDER BY stock_actual ASC, nombre ASC")
    fun observarProductosStockBajo(): Flow<List<ProductoEntity>>

    @Query(
        """
        SELECT * FROM productos
        ORDER BY
            CASE WHEN stock_actual < 5 THEN 0 ELSE 1 END,
            stock_actual ASC,
            nombre ASC
        """
    )
    fun observarProductosOrdenadosPorStock(): Flow<List<ProductoEntity>>

    @Transaction
    suspend fun registrarMovimientoInventario(
        idProducto: Int,
        tipo: String,
        cantidad: Int,
        motivo: String,
        fecha: String
    ) {
        val producto = obtenerProductoPorId(idProducto)
            ?: throw IllegalArgumentException("Producto no encontrado")

        if (cantidad <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor que cero")
        }

        val tipoNormalizado = tipo.uppercase()
        val stockAnterior = producto.stockActual
        val stockNuevo = when (tipoNormalizado) {
            "ENTRADA" -> stockAnterior + cantidad
            "SALIDA" -> {
                if (stockAnterior < cantidad) {
                    throw IllegalArgumentException("No se permite una salida sin stock disponible")
                }
                stockAnterior - cantidad
            }
            else -> throw IllegalArgumentException("Tipo de movimiento no válido")
        }

        actualizarStockProducto(idProducto, stockNuevo)
        insertarMovimientoInventario(
            MovimientoInventarioEntity(
                idProducto = idProducto,
                tipo = tipoNormalizado,
                cantidad = cantidad,
                stockAnterior = stockAnterior,
                stockNuevo = stockNuevo,
                motivo = motivo.ifBlank { "Sin motivo especificado" },
                fecha = fecha
            )
        )
    }

    @Transaction
    suspend fun registrarDevolucion(
        codigoVenta: String,
        idProducto: Int,
        cantidad: Int,
        motivo: String,
        estadoProducto: String,
        fecha: String
    ) {
        val producto = obtenerProductoPorId(idProducto)
            ?: throw IllegalArgumentException("Producto no encontrado")

        if (codigoVenta.isBlank()) {
            throw IllegalArgumentException("Ingresa el código de venta o factura")
        }
        if (!InventarioBusinessRules.validarCantidad(cantidad)) {
            throw IllegalArgumentException("La cantidad a devolver debe ser mayor que cero")
        }

        val reintegrar = InventarioBusinessRules.debeReintegrarStock(estadoProducto)
        val stockAnterior = producto.stockActual
        val stockNuevo = InventarioBusinessRules.calcularStockReintegrado(
            stockActual = stockAnterior,
            cantidadDevuelta = cantidad,
            estadoProducto = estadoProducto
        )
        val motivoFinal = motivo.ifBlank { "Sin motivo especificado" }

        if (reintegrar) {
            incrementarStockAtomicamente(idProducto, cantidad)
            insertarMovimientoInventario(
                MovimientoInventarioEntity(
                    idProducto = idProducto,
                    tipo = "DEVOLUCION",
                    cantidad = cantidad,
                    stockAnterior = stockAnterior,
                    stockNuevo = stockNuevo,
                    motivo = "Devolución útil: $motivoFinal",
                    fecha = fecha
                )
            )
        } else {
            insertarMerma(
                MermaEntity(
                    idProducto = idProducto,
                    cantidad = cantidad,
                    motivo = motivoFinal,
                    origen = "Devolución dañada / defectuosa",
                    codigoReferencia = codigoVenta.trim(),
                    fecha = fecha
                )
            )
        }

        insertarDevolucion(
            DevolucionEntity(
                codigoVenta = codigoVenta.trim(),
                idProducto = idProducto,
                cantidad = cantidad,
                motivo = motivoFinal,
                estadoProducto = estadoProducto,
                reintegradoStock = reintegrar,
                stockAnterior = stockAnterior,
                stockNuevo = stockNuevo,
                fecha = fecha
            )
        )
    }
}
