package com.example.proyectofinalgrupo3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectofinalgrupo3.ui.screens.PantallaAdminDashboard
import com.example.proyectofinalgrupo3.ui.screens.PantallaAdminPedidos
import com.example.proyectofinalgrupo3.ui.screens.PantallaCarrito
import com.example.proyectofinalgrupo3.ui.screens.PantallaCatalogo
import com.example.proyectofinalgrupo3.ui.screens.PantallaCheckout
import com.example.proyectofinalgrupo3.ui.screens.PantallaDashboardStock
import com.example.proyectofinalgrupo3.ui.screens.PantallaDetalleProducto
import com.example.proyectofinalgrupo3.ui.screens.PantallaDevoluciones
import com.example.proyectofinalgrupo3.ui.screens.PantallaInventario
import com.example.proyectofinalgrupo3.ui.screens.PantallaLogin
import com.example.proyectofinalgrupo3.ui.screens.PantallaRegistroProducto
import com.example.proyectofinalgrupo3.ui.viewmodel.AdminPedidosViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.CarritoViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.CatalogoViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.DetalleProductoViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.DevolucionesViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.InventarioViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.LoginViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.RegistroProductoViewModel
import com.example.proyectofinalgrupo3.ui.viewmodel.StockDashboardViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    catalogoViewModel: CatalogoViewModel,
    registroProductoViewModel: RegistroProductoViewModel,
    inventarioViewModel: InventarioViewModel,
    devolucionesViewModel: DevolucionesViewModel,
    stockDashboardViewModel: StockDashboardViewModel,
    adminPedidosViewModel: AdminPedidosViewModel,
    detalleProductoViewModel: DetalleProductoViewModel,
    carritoViewModel: CarritoViewModel
) {
    val navController = rememberNavController()

    var usuarioSesion by remember {
        mutableStateOf<Int?>(null)
    }

    var rolSesion by remember {
        mutableStateOf<Int?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            PantallaLogin(
                viewModel = loginViewModel,
                onLoginSuccess = { usuario ->
                    usuarioSesion = usuario.idUsuario
                    rolSesion = usuario.idRol

                    val destino = if (usuario.idRol == 1) {
                        "admin_dashboard"
                    } else {
                        "catalogo"
                    }

                    navController.navigate(destino) {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("admin_dashboard") {
            PantallaAdminDashboard(
                onNavigateToRegistro = {
                    navController.navigate("registro_producto")
                },
                onNavigateToInventario = {
                    navController.navigate("inventario")
                },
                onNavigateToDevoluciones = {
                    navController.navigate("devoluciones")
                },
                onNavigateToDashboardStock = {
                    navController.navigate("dashboard_stock")
                },
                onNavigateToPedidos = {
                    navController.navigate("admin_pedidos")
                },
                onNavigateToCatalogo = {
                    navController.navigate("catalogo")
                },
                onLogout = {
                    loginViewModel.resetState()
                    usuarioSesion = null
                    rolSesion = null

                    navController.navigate("login") {
                        popUpTo("admin_dashboard") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("registro_producto") {
            PantallaRegistroProducto(
                viewModel = registroProductoViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("inventario") {
            PantallaInventario(
                viewModel = inventarioViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("devoluciones") {
            PantallaDevoluciones(
                viewModel = devolucionesViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard_stock") {
            PantallaDashboardStock(
                viewModel = stockDashboardViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_pedidos") {
            PantallaAdminPedidos(
                viewModel = adminPedidosViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("catalogo") {
            PantallaCatalogo(
                viewModel = catalogoViewModel,
                // Marco mejora
                // Aquí agregué la validación para que el catálogo funcione diferente según el rol.
                // Si ingreso como administrador, la flecha me regresa al panel anterior.
                // Si ingreso como cliente, la flecha me permite cerrar sesión y volver al login.
                onBack = if (rolSesion == 1) {
                    {
                        navController.popBackStack()
                    }
                } else {
                    {
                        loginViewModel.resetState()
                        usuarioSesion = null
                        rolSesion = null

                        navController.navigate("login") {
                            popUpTo("catalogo") {
                                inclusive = true
                            }
                        }
                    }
                },

                onVerDetalle = { idProducto ->
                    navController.navigate("detalle_producto/$idProducto")
                },
                onVerCarrito = {
                    navController.navigate("carrito")
                }
            )
        }

        composable(
            route = "detalle_producto/{idProducto}",
            arguments = listOf(
                navArgument("idProducto") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val idProducto = backStackEntry.arguments?.getInt("idProducto") ?: 0

            PantallaDetalleProducto(
                idProducto = idProducto,
                idUsuario = usuarioSesion ?: 0,
                viewModel = detalleProductoViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToCarrito = {
                    navController.navigate("carrito")
                }
            )
        }

        composable("carrito") {
            PantallaCarrito(
                idUsuario = usuarioSesion ?: 0,
                viewModel = carritoViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate("checkout")
                },
                onSuccess = {
                    navController.navigate("catalogo") {
                        popUpTo("catalogo") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("checkout") {
            PantallaCheckout(
                onBack = {
                    navController.popBackStack()
                },
                onConfirmPurchase = {
                    carritoViewModel.finalizarCompra(usuarioSesion ?: 0)

                    navController.navigate("catalogo") {
                        popUpTo("catalogo") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}