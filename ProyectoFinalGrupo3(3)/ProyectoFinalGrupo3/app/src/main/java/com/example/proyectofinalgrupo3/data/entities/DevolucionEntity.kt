package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "devoluciones",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id_producto"],
            childColumns = ["id_producto"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["id_producto"]),
        Index(value = ["codigo_venta"])
    ]
)
data class DevolucionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_devolucion")
    val idDevolucion: Int = 0,

    @ColumnInfo(name = "codigo_venta")
    val codigoVenta: String,

    @ColumnInfo(name = "id_producto")
    val idProducto: Int,

    val cantidad: Int,
    val motivo: String,

    @ColumnInfo(name = "estado_producto")
    val estadoProducto: String,

    @ColumnInfo(name = "reintegrado_stock")
    val reintegradoStock: Boolean,

    @ColumnInfo(name = "stock_anterior")
    val stockAnterior: Int,

    @ColumnInfo(name = "stock_nuevo")
    val stockNuevo: Int,

    val fecha: String
)
