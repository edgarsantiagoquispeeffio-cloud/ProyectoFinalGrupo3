package com.example.proyectofinalgrupo3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    foreignKeys = [
        ForeignKey(
            entity = RolEntity::class,
            parentColumns = ["id_rol"],
            childColumns = ["id_rol"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_rol"])]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,
    @ColumnInfo(name = "id_rol")
    val idRol: Int,
    @ColumnInfo(name = "nombre_completo")
    val nombreCompleto: String,
    val correo: String,
    val contrasena: String
)
