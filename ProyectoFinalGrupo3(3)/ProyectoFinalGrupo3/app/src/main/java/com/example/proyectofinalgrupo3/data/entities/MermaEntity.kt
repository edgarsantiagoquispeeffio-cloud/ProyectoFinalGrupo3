package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mermas_bajas",
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
data class MermaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_merma")
    val idMerma: Int = 0,

    @ColumnInfo(name = "id_producto")
    val idProducto: Int,

    val cantidad: Int,
    val motivo: String,
    val origen: String,

    @ColumnInfo(name = "codigo_referencia")
    val codigoReferencia: String,

    val fecha: String
)
