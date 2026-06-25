package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalle_pedidos",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["id_pedido"],
            childColumns = ["id_pedido"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id_producto"],
            childColumns = ["id_producto"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["id_pedido"]),
        Index(value = ["id_producto"])
    ]
)
data class DetallePedidoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_detalle")
    val idDetalle: Int = 0,
    @ColumnInfo(name = "id_pedido")
    val idPedido: Int,
    @ColumnInfo(name = "id_producto")
    val idProducto: Int,
    val cantidad: Int,
    @ColumnInfo(name = "precio_unitario")
    val precioUnitario: Double
)
