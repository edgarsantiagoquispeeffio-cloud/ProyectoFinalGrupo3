package com.example.proyectofinalgrupo3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.data.model.InventarioKpi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StockDashboardViewModel(private val productoDao: ProductoDao) : ViewModel() {

    val indicadores: StateFlow<InventarioKpi> = productoDao.observarIndicadoresInventario()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InventarioKpi()
        )

    val productosStockBajo: StateFlow<List<ProductoEntity>> = productoDao.observarProductosStockBajo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val productosOrdenados: StateFlow<List<ProductoEntity>> = productoDao.observarProductosOrdenadosPorStock()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
