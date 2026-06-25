package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.business.InventarioBusinessRules
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.DevolucionEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DevolucionesViewModel(private val productoDao: ProductoDao) : ViewModel() {

    val productos: StateFlow<List<ProductoEntity>> = productoDao.obtenerCatalogo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val devoluciones: StateFlow<List<DevolucionEntity>> = productoDao.observarDevoluciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var codigoVenta by mutableStateOf("")
    var idProductoSeleccionado by mutableStateOf("")
    var cantidad by mutableStateOf("")
    var motivo by mutableStateOf("")
    var estadoProducto by mutableStateOf(InventarioBusinessRules.ESTADO_UTIL)
    var showErrors by mutableStateOf(false)
    var isSaving by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }

    fun cambiarEstado(estado: String) {
        estadoProducto = estado
    }

    fun registrarDevolucion() {
        val idProducto = idProductoSeleccionado.toIntOrNull()
        val cantidadInt = cantidad.toIntOrNull()

        if (codigoVenta.isBlank() || idProducto == null || cantidadInt == null || cantidadInt <= 0) {
            showErrors = true
            return
        }

        viewModelScope.launch {
            isSaving = true
            try {
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                withContext(Dispatchers.IO) {
                    productoDao.registrarDevolucion(
                        codigoVenta = codigoVenta,
                        idProducto = idProducto,
                        cantidad = cantidadInt,
                        motivo = motivo,
                        estadoProducto = estadoProducto,
                        fecha = fecha
                    )
                }

                val mensaje = if (InventarioBusinessRules.debeReintegrarStock(estadoProducto)) {
                    "Devolución registrada. Stock reintegrado automáticamente."
                } else {
                    "Devolución registrada como merma. No se sumó al stock."
                }
                _eventFlow.emit(UiEvent.Success(mensaje))
                resetFields()
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.Error(e.message ?: "No se pudo registrar la devolución"))
            } finally {
                isSaving = false
            }
        }
    }

    private fun resetFields() {
        codigoVenta = ""
        cantidad = ""
        motivo = ""
        estadoProducto = InventarioBusinessRules.ESTADO_UTIL
        showErrors = false
    }
}
