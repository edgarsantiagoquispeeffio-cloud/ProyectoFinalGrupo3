package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colecciones")
data class ColeccionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_coleccion")
    val idColeccion: Int = 0,
    val nombre: String
)
