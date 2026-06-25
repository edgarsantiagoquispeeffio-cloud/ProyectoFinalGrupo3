package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.MovimientoInventarioEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventarioViewModel(private val productoDao: ProductoDao) : ViewModel() {

    val productos: StateFlow<List<ProductoEntity>> = productoDao.obtenerCatalogo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val movimientos: StateFlow<List<MovimientoInventarioEntity>> = productoDao.obtenerMovimientosInventario()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var idProductoSeleccionado by mutableStateOf("")
    var tipoMovimiento by mutableStateOf("ENTRADA")
    var cantidad by mutableStateOf("")
    var motivo by mutableStateOf("")
    var showErrors by mutableStateOf(false)
    var isSaving by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class Error(val message: String) : UiEvent()
    }

    fun cambiarTipo(tipo: String) {
        tipoMovimiento = tipo
    }

    fun registrarMovimiento() {
        val idProducto = idProductoSeleccionado.toIntOrNull()
        val cantidadInt = cantidad.toIntOrNull()

        if (idProducto == null || cantidadInt == null || cantidadInt <= 0) {
            showErrors = true
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isSaving = true
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                productoDao.registrarMovimientoInventario(
                    idProducto = idProducto,
                    tipo = tipoMovimiento,
                    cantidad = cantidadInt,
                    motivo = motivo,
                    fecha = fecha
                )
                _eventFlow.emit(UiEvent.Success("Movimiento registrado y stock actualizado"))
                cantidad = ""
                motivo = ""
                showErrors = false
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.Error(e.message ?: "No se pudo registrar el movimiento"))
            } finally {
                isSaving = false
            }
        }
    }
}
