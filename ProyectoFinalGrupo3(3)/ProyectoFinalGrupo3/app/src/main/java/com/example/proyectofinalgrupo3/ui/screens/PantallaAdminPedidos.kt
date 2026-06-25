package com.example.proyectofinalgrupo3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinalgrupo3.ui.viewmodel.AdminPedidosViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdminPedidos(
    viewModel: AdminPedidosViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auditoría de Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(text = "Seleccione un cliente:", style = MaterialTheme.typography.titleMedium)
            
            // Lista de clientes
            LazyColumn(modifier = Modifier.weight(0.4f)) {
                items(viewModel.clientes) { cliente ->
                    ListItem(
                        headlineContent = { Text(cliente.nombreCompleto) },
                        supportingContent = { Text(cliente.correo) },
                        leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.clickable { viewModel.seleccionarCliente(cliente) },
                        colors = ListItemDefaults.colors(
                            containerColor = if (viewModel.clienteSeleccionado?.idUsuario == cliente.idUsuario) 
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Historial: ${viewModel.clienteSeleccionado?.nombreCompleto ?: "Ninguno"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Lista de pedidos del cliente seleccionado
            LazyColumn(modifier = Modifier.weight(0.6f)) {
                items(viewModel.pedidosSeleccionados) { pedido ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold)
                                Text(text = "Fecha: ${pedido.fechaPedido}")
                                Text(
                                    text = "Total: $${String.format(Locale.getDefault(), "%.2f", pedido.total)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
