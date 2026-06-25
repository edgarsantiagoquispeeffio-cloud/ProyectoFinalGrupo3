package com.example.proyectofinalgrupo3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinalgrupo3.data.database.AppDatabase
import com.example.proyectofinalgrupo3.data.repository.AuthRepository
import com.example.proyectofinalgrupo3.data.repository.CatalogoRepository
import com.example.proyectofinalgrupo3.ui.factory.ViewModelFactory
import com.example.proyectofinalgrupo3.ui.navigation.AppNavigation
import com.example.proyectofinalgrupo3.ui.theme.ProyectoFinalGrupo3Theme
import com.example.proyectofinalgrupo3.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val authRepository = AuthRepository(database.usuarioDao())
        val catalogoRepository = CatalogoRepository(database.productoDao())

        // Inicialización de ViewModels con la Factory mejorada
        val loginViewModel = ViewModelProvider(this, ViewModelFactory(authRepository))[LoginViewModel::class.java]
        val catalogoViewModel = ViewModelProvider(this, ViewModelFactory(catalogoRepository))[CatalogoViewModel::class.java]
        val registroViewModel = ViewModelProvider(this, ViewModelFactory(database.productoDao()))[RegistroProductoViewModel::class.java]
        val inventarioViewModel = ViewModelProvider(this, ViewModelFactory(database.productoDao()))[InventarioViewModel::class.java]
        val devolucionesViewModel = ViewModelProvider(this, ViewModelFactory(database.productoDao()))[DevolucionesViewModel::class.java]
        val stockDashboardViewModel = ViewModelProvider(this, ViewModelFactory(database.productoDao()))[StockDashboardViewModel::class.java]
        val adminPedidosViewModel = ViewModelProvider(this, ViewModelFactory(database.ecommerceDao()))[AdminPedidosViewModel::class.java]
        val detalleViewModel = ViewModelProvider(this, ViewModelFactory(database.ecommerceDao()))[DetalleProductoViewModel::class.java]
        val carritoViewModel = ViewModelProvider(this, ViewModelFactory(database.ecommerceDao()))[CarritoViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ProyectoFinalGrupo3Theme {
                AppNavigation(
                    loginViewModel = loginViewModel,
                    catalogoViewModel = catalogoViewModel,
                    registroProductoViewModel = registroViewModel,
                    inventarioViewModel = inventarioViewModel,
                    devolucionesViewModel = devolucionesViewModel,
                    stockDashboardViewModel = stockDashboardViewModel,
                    adminPedidosViewModel = adminPedidosViewModel,
                    detalleProductoViewModel = detalleViewModel,
                    carritoViewModel = carritoViewModel
                )
            }
        }
    }
}
