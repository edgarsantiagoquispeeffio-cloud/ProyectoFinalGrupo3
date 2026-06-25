package com.example.proyectofinalgrupo3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinalgrupo3.data.business.InventarioBusinessRules
import com.example.proyectofinalgrupo3.data.entities.DevolucionEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.ui.viewmodel.DevolucionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDevoluciones(
    viewModel: DevolucionesViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    val devoluciones by viewModel.devoluciones.collectAsState()
    val cantidadInt = viewModel.cantidad.toIntOrNull() ?: 0
    val productoSeleccionado = productos.firstOrNull {
        it.idProducto.toString() == viewModel.idProductoSeleccionado
    }
    val reintegraStock = InventarioBusinessRules.debeReintegrarStock(viewModel.estadoProducto)
    val stockProyectado = productoSeleccionado?.let { producto ->
        InventarioBusinessRules.calcularStockReintegrado(
            stockActual = producto.stockActual,
            cantidadDevuelta = cantidadInt,
            estadoProducto = viewModel.estadoProducto
        )
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DevolucionesViewModel.UiEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is DevolucionesViewModel.UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devoluciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Registro y reintegro de devoluciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Si el producto está útil, el sistema reintegra el stock. Si está dañado, se registra como merma y no aumenta el inventario.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.codigoVenta,
                        onValueChange = { viewModel.codigoVenta = it.take(30) },
                        label = { Text("ID / Código de venta o factura *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.showErrors && viewModel.codigoVenta.isBlank(),
                        singleLine = true
                    )

                    DropdownSelector(
                        label = "Producto *",
                        options = productos.map { producto ->
                            producto.idProducto.toString() to "${producto.nombre} - ${producto.sku}"
                        },
                        selectedOption = viewModel.idProductoSeleccionado,
                        onOptionSelected = { viewModel.idProductoSeleccionado = it },
                        isError = viewModel.showErrors && viewModel.idProductoSeleccionado.isBlank()
                    )

                    productoSeleccionado?.let { producto ->
                        DevolucionStockPreview(
                            producto = producto,
                            stockProyectado = stockProyectado,
                            reintegraStock = reintegraStock
                        )
                    }

                    OutlinedTextField(
                        value = viewModel.cantidad,
                        onValueChange = { viewModel.cantidad = it.filter { char -> char.isDigit() }.take(5) },
                        label = { Text("Cantidad a devolver *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.showErrors && cantidadInt <= 0,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = viewModel.motivo,
                        onValueChange = { viewModel.motivo = it.take(120) },
                        label = { Text("Motivo de la devolución") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Text("Estado del producto", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = viewModel.estadoProducto == InventarioBusinessRules.ESTADO_UTIL,
                            onClick = { viewModel.cambiarEstado(InventarioBusinessRules.ESTADO_UTIL) },
                            label = { Text("Útil") }
                        )
                        FilterChip(
                            selected = viewModel.estadoProducto == InventarioBusinessRules.ESTADO_DANADO,
                            onClick = { viewModel.cambiarEstado(InventarioBusinessRules.ESTADO_DANADO) },
                            label = { Text("Dañado") }
                        )
                    }

                    Text(
                        text = if (reintegraStock) {
                            "Resultado: se aprobará el reintegro automático al stock."
                        } else {
                            "Resultado: se registrará en mermas/bajas y no se sumará al stock."
                        },
                        color = if (reintegraStock) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (viewModel.showErrors) {
                        Text(
                            text = "Completa el código, producto y una cantidad mayor que cero.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = { viewModel.registrarDevolucion() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isSaving
                    ) {
                        Text(if (viewModel.isSaving) "Guardando..." else "Registrar devolución")
                    }
                }
            }

            Text(
                text = "Últimas devoluciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (devoluciones.isEmpty()) {
                Text("Aún no hay devoluciones registradas.")
            } else {
                devoluciones.take(6).forEach { devolucion ->
                    val producto = productos.firstOrNull { it.idProducto == devolucion.idProducto }
                    DevolucionRow(devolucion = devolucion, producto = producto)
                }
            }

            Spacer(
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

@Composable
private fun DevolucionStockPreview(
    producto: ProductoEntity,
    stockProyectado: Int?,
    reintegraStock: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("SKU: ${producto.sku}", fontWeight = FontWeight.Bold)
            Text("Stock actual: ${producto.stockActual}")
            if (stockProyectado != null) {
                Text("Stock después del registro: $stockProyectado")
            }
            Text(
                text = if (reintegraStock) "Acción: reintegra stock" else "Acción: no reintegra stock",
                color = if (reintegraStock) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun DevolucionRow(devolucion: DevolucionEntity, producto: ProductoEntity?) {
    val reintegrado = devolucion.reintegradoStock
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reintegrado) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(if (reintegrado) "Reintegrado" else "Merma") }
                )
                Text(devolucion.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(producto?.nombre ?: "Producto ID ${devolucion.idProducto}", fontWeight = FontWeight.Bold)
            Text("Venta/Factura: ${devolucion.codigoVenta}")
            Text("Cantidad: ${devolucion.cantidad} | Stock: ${devolucion.stockAnterior} → ${devolucion.stockNuevo}")
            Text("Estado: ${devolucion.estadoProducto}")
            Text("Motivo: ${devolucion.motivo}")
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}