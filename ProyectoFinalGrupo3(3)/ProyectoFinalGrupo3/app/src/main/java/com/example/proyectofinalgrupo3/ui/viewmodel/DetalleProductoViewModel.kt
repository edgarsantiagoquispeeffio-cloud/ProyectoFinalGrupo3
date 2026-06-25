package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.EcommerceDao
import com.example.proyectofinalgrupo3.data.entities.CarritoEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.launch

class DetalleProductoViewModel(private val ecommerceDao: EcommerceDao) : ViewModel() {

    var producto by mutableStateOf<ProductoEntity?>(null)
    var cantidad by mutableStateOf(1)
    var mensaje by mutableStateOf("")

    fun cargarProducto(idProducto: Int) {
        viewModelScope.launch {
            producto = ecommerceDao.getProductoById(idProducto)
        }
    }

    fun añadirAlCarrito(idUsuario: Int) {
        val prod = producto ?: return
        if (prod.stockActual < cantidad) {
            mensaje = "No hay suficiente stock"
            return
        }

        viewModelScope.launch {
            try {
                ecommerceDao.addOrUpdateCart(idUsuario, prod.idProducto, cantidad)
                mensaje = "Producto añadido al carrito"
            } catch (e: Exception) {
                mensaje = "Error al añadir: ${e.message}"
            }
        }
    }
}
