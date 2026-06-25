package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.EcommerceDao
import com.example.proyectofinalgrupo3.data.entities.PedidoEntity
import com.example.proyectofinalgrupo3.data.entities.UsuarioEntity
import kotlinx.coroutines.launch

class AdminPedidosViewModel(private val ecommerceDao: EcommerceDao) : ViewModel() {

    var clientes by mutableStateOf<List<UsuarioEntity>>(emptyList())
    var pedidosSeleccionados by mutableStateOf<List<PedidoEntity>>(emptyList())
    var clienteSeleccionado by mutableStateOf<UsuarioEntity?>(null)

    init {
        cargarClientes()
    }

    private fun cargarClientes() {
        viewModelScope.launch {
            // Filtramos por idRol = 3 (Clientes)
            clientes = ecommerceDao.getAllUsuarios().filter { it.idRol == 3 }
        }
    }

    fun seleccionarCliente(usuario: UsuarioEntity) {
        clienteSeleccionado = usuario
        viewModelScope.launch {
            pedidosSeleccionados = ecommerceDao.getPedidosByUsuario(usuario.idUsuario)
        }
    }
}
