package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ColeccionEntity::class,
            parentColumns = ["id_coleccion"],
            childColumns = ["id_coleccion"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["id_categoria"]),
        Index(value = ["id_coleccion"]),
        Index(value = ["sku"], unique = true)
    ]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_producto")
    val idProducto: Int = 0,
    val sku: String,
    val nombre: String,
    @ColumnInfo(name = "id_categoria")
    val idCategoria: Int,
    @ColumnInfo(name = "id_coleccion")
    val idColeccion: Int?,
    val talla: String,
    val color: String,
    val precio: Double,
    @ColumnInfo(name = "stock_actual")
    val stockActual: Int,
    @ColumnInfo(name = "stock_minimo")
    val stockMinimo: Int
)
