package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos_inventario",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id_producto"],
            childColumns = ["id_producto"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["id_producto"])]
)
data class MovimientoInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_movimiento")
    val idMovimiento: Int = 0,

    @ColumnInfo(name = "id_producto")
    val idProducto: Int,

    val tipo: String,
    val cantidad: Int,

    @ColumnInfo(name = "stock_anterior")
    val stockAnterior: Int,

    @ColumnInfo(name = "stock_nuevo")
    val stockNuevo: Int,

    val motivo: String,
    val fecha: String
)
