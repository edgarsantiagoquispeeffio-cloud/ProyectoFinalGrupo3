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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CartItem(
    val carrito: CarritoEntity,
    val producto: ProductoEntity
)

class CarritoViewModel(private val ecommerceDao: EcommerceDao) : ViewModel() {

    var items by mutableStateOf<List<CartItem>>(emptyList())
    var total by mutableStateOf(0.0)
    var compraExitosa by mutableStateOf(false)

    fun cargarCarrito(idUsuario: Int) {
        viewModelScope.launch {
            val carritoItems = ecommerceDao.getCarritoByUsuario(idUsuario)
            val lista = mutableListOf<CartItem>()
            var suma = 0.0
            for (item in carritoItems) {
                val producto = ecommerceDao.getProductoById(item.idProducto)
                if (producto != null) {
                    lista.add(CartItem(item, producto))
                    suma += producto.precio * item.cantidad
                }
            }
            items = lista
            total = suma
        }
    }

    fun eliminarItem(item: CarritoEntity, idUsuario: Int) {
        viewModelScope.launch {
            ecommerceDao.deleteCarritoItem(item)
            cargarCarrito(idUsuario)
        }
    }

    fun finalizarCompra(idUsuario: Int) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fecha = sdf.format(Date())
            ecommerceDao.realizarCompra(idUsuario, fecha)
            compraExitosa = true
            items = emptyList()
            total = 0.0
        }
    }
}
