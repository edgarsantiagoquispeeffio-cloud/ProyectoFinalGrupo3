package com.example.proyectofinalgrupo3.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinalgrupo3.data.dao.EcommerceDao
import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.repository.AuthRepository
import com.example.proyectofinalgrupo3.data.repository.CatalogoRepository
import com.example.proyectofinalgrupo3.ui.viewmodel.*

class ViewModelFactory(private val dependency: Any) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(dependency as AuthRepository) as T
            }
            modelClass.isAssignableFrom(CatalogoViewModel::class.java) -> {
                CatalogoViewModel((dependency as CatalogoRepository).productoDao) as T
            }
            modelClass.isAssignableFrom(RegistroProductoViewModel::class.java) -> {
                RegistroProductoViewModel(dependency as ProductoDao) as T
            }
            modelClass.isAssignableFrom(AdminPedidosViewModel::class.java) -> {
                AdminPedidosViewModel(dependency as EcommerceDao) as T
            }
            modelClass.isAssignableFrom(InventarioViewModel::class.java) -> {
                InventarioViewModel(dependency as ProductoDao) as T
            }
            modelClass.isAssignableFrom(DevolucionesViewModel::class.java) -> {
                DevolucionesViewModel(dependency as ProductoDao) as T
            }
            modelClass.isAssignableFrom(StockDashboardViewModel::class.java) -> {
                StockDashboardViewModel(dependency as ProductoDao) as T
            }
            modelClass.isAssignableFrom(DetalleProductoViewModel::class.java) -> {
                DetalleProductoViewModel(dependency as EcommerceDao) as T
            }
            modelClass.isAssignableFrom(CarritoViewModel::class.java) -> {
                CarritoViewModel(dependency as EcommerceDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
