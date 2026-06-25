package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.flow.*

class CatalogoViewModel(private val productoDao: ProductoDao) : ViewModel() {

    // Filtros
    val categoriaFiltro = MutableStateFlow<Int?>(null)
    val tallaFiltro = MutableStateFlow<String?>(null)
    val colorFiltro = MutableStateFlow<String?>(null)

    // Catálogo filtrado reactivo
    val productosFiltrados: StateFlow<List<ProductoEntity>> = combine(
        productoDao.obtenerCatalogo(),
        categoriaFiltro,
        tallaFiltro,
        colorFiltro
    ) { listaOriginal, cat, talla, color ->
        listaOriginal.filter { producto ->
            (cat == null || producto.idCategoria == cat) &&
            (talla == null || producto.talla.equals(talla, ignoreCase = true)) &&
            (color == null || producto.color.equals(color, ignoreCase = true))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun limpiarFiltros() {
        categoriaFiltro.value = null
        tallaFiltro.value = null
        colorFiltro.value = null
    }

    fun actualizarCategoria(id: Int?) { categoriaFiltro.value = id }
    fun actualizarTalla(talla: String?) { tallaFiltro.value = talla }
    fun actualizarColor(color: String?) { colorFiltro.value = color }
}
