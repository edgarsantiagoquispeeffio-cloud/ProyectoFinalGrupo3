package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["id_usuario"])]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pedido")
    val idPedido: Int = 0,
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,
    @ColumnInfo(name = "fecha_pedido")
    val fechaPedido: String,
    val total: Double,
    val estado: String
)
