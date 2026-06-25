package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.entities.UsuarioEntity
import com.example.proyectofinalgrupo3.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: UsuarioEntity) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(correo: String, contrasena: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = LoginState.Loading
            try {
                val usuario = authRepository.hacerLogin(correo, contrasena)
                if (usuario != null) {
                    _uiState.value = LoginState.Success(usuario)
                } else {
                    _uiState.value = LoginState.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _uiState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginState.Idle
    }
}
