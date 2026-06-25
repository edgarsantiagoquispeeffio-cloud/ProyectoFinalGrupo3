package com.example.proyectofinalgrupo3.data.repository

import com.example.proyectofinalgrupo3.data.dao.UsuarioDao
import com.example.proyectofinalgrupo3.data.entities.UsuarioEntity

class AuthRepository(private val usuarioDao: UsuarioDao) {
    suspend fun hacerLogin(correo: String, contrasena: String): UsuarioEntity? {
        return usuarioDao.login(correo, contrasena)
    }
}
