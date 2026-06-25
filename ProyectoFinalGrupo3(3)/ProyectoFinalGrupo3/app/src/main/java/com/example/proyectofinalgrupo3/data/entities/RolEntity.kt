package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RolEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rol")
    val idRol: Int = 0,
    val nombre: String
)
