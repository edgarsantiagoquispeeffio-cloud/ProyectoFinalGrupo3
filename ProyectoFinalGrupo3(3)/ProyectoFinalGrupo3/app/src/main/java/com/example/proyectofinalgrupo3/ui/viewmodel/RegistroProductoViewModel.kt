package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegistroProductoViewModel(private val productoDao: ProductoDao) : ViewModel() {

    var sku by mutableStateOf("")
    var nombre by mutableStateOf("")
    var idCategoria by mutableStateOf("")
    var idColeccion by mutableStateOf("")
    var talla by mutableStateOf("")
    var color by mutableStateOf("")
    var precio by mutableStateOf("")
    var stockActual by mutableStateOf("")
    var stockMinimo by mutableStateOf("")

    var showErrors by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        object Success : UiEvent()
        data class Error(val message: String) : UiEvent()
    }

    fun registrarProducto() {
        if (validateFields()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val producto = ProductoEntity(
                        sku = sku,
                        nombre = nombre,
                        idCategoria = idCategoria.toInt(),
                        idColeccion = idColeccion.toIntOrNull(),
                        talla = talla,
                        color = color,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        stockActual = stockActual.toIntOrNull() ?: 0,
                        stockMinimo = stockMinimo.toIntOrNull() ?: 0
                    )
                    productoDao.insertarProducto(producto)
                    _eventFlow.emit(UiEvent.Success)
                    resetFields()
                } catch (e: Exception) {
                    _eventFlow.emit(UiEvent.Error("Error al guardar: ${e.message}"))
                }
            }
        } else {
            showErrors = true
        }
    }

    private fun validateFields(): Boolean {
        return sku.isNotBlank() &&
                nombre.isNotBlank() &&
                idCategoria.isNotBlank() &&
                talla.isNotBlank() &&
                color.isNotBlank()
    }

    private fun resetFields() {
        sku = ""
        nombre = ""
        idCategoria = ""
        idColeccion = ""
        talla = ""
        color = ""
        precio = ""
        stockActual = ""
        stockMinimo = ""
        showErrors = false
    }
}
