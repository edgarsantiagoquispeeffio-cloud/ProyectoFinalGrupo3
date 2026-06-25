package com.example.proyectofinalgrupo3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.proyectofinalgrupo3.data.entities.MovimientoInventarioEntity
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.ui.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInventario(
    viewModel: InventarioViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()
    val productoSeleccionado = productos.firstOrNull {
        it.idProducto.toString() == viewModel.idProductoSeleccionado
    }
    val cantidadInt = viewModel.cantidad.toIntOrNull() ?: 0
    val stockProyectado = when (viewModel.tipoMovimiento) {
        "ENTRADA" -> productoSeleccionado?.stockActual?.plus(cantidadInt)
        "SALIDA" -> productoSeleccionado?.stockActual?.minus(cantidadInt)
        else -> productoSeleccionado?.stockActual
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is InventarioViewModel.UiEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is InventarioViewModel.UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Registrar entradas y salidas de inventario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Actualiza el stock en tiempo real y evita salidas cuando no hay unidades disponibles.",
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
                    DropdownSelector(
                        label = "Producto / variante *",
                        options = productos.map { producto ->
                            producto.idProducto.toString() to "${producto.nombre} - ${producto.talla} - ${producto.color}"
                        },
                        selectedOption = viewModel.idProductoSeleccionado,
                        onOptionSelected = { viewModel.idProductoSeleccionado = it },
                        isError = viewModel.showErrors && viewModel.idProductoSeleccionado.isBlank()
                    )

                    productoSeleccionado?.let { producto ->
                        StockActualCard(producto = producto, stockProyectado = stockProyectado)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = viewModel.tipoMovimiento == "ENTRADA",
                            onClick = { viewModel.cambiarTipo("ENTRADA") },
                            label = { Text("Entrada") }
                        )
                        FilterChip(
                            selected = viewModel.tipoMovimiento == "SALIDA",
                            onClick = { viewModel.cambiarTipo("SALIDA") },
                            label = { Text("Salida") }
                        )
                    }

                    OutlinedTextField(
                        value = viewModel.cantidad,
                        onValueChange = { viewModel.cantidad = it.filter { char -> char.isDigit() } },
                        label = { Text("Cantidad *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.showErrors && cantidadInt <= 0
                    )

                    OutlinedTextField(
                        value = viewModel.motivo,
                        onValueChange = { viewModel.motivo = it },
                        label = { Text("Motivo / observación") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    if (viewModel.showErrors) {
                        Text(
                            text = "Completa el producto y coloca una cantidad mayor que cero.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (viewModel.tipoMovimiento == "SALIDA" && stockProyectado != null && stockProyectado < 0) {
                        Text(
                            text = "No se permite registrar una salida mayor al stock disponible.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.registrarMovimiento() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isSaving
                    ) {
                        Text(if (viewModel.isSaving) "Guardando..." else "Registrar movimiento")
                    }
                }
            }

            Text(
                text = "Stock actualizado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            productos.forEach { producto ->
                ProductoStockRow(producto)
            }

            Text(
                text = "Últimos movimientos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (movimientos.isEmpty()) {
                Text("Aún no hay movimientos registrados.")
            } else {
                movimientos.take(6).forEach { movimiento ->
                    val producto = productos.firstOrNull { it.idProducto == movimiento.idProducto }
                    MovimientoRow(movimiento = movimiento, producto = producto)
                }
            }
        }
    }
}

@Composable
private fun StockActualCard(producto: ProductoEntity, stockProyectado: Int?) {
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
            Text("Stock mínimo: ${producto.stockMinimo}")
            if (stockProyectado != null) {
                Text("Stock después del movimiento: $stockProyectado")
            }
        }
    }
}

@Composable
private fun ProductoStockRow(producto: ProductoEntity) {
    val stockBajo = producto.stockActual <= producto.stockMinimo
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (stockBajo) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("${producto.talla} / ${producto.color} / SKU ${producto.sku}")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Stock: ${producto.stockActual}", fontWeight = FontWeight.Bold)
                if (stockBajo) {
                    Text("Stock bajo", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun MovimientoRow(movimiento: MovimientoInventarioEntity, producto: ProductoEntity?) {
    val etiquetaTipo = when (movimiento.tipo) {
        "ENTRADA" -> "Entrada"
        "SALIDA" -> "Salida"
        "DEVOLUCION" -> "Devolución"
        else -> movimiento.tipo.lowercase().replaceFirstChar { it.titlecase() }
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(etiquetaTipo) }
                )
                Text(movimiento.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(producto?.nombre ?: "Producto ID ${movimiento.idProducto}", fontWeight = FontWeight.Bold)
            Text("Cantidad: ${movimiento.cantidad} | Stock: ${movimiento.stockAnterior} → ${movimiento.stockNuevo}")
            Text("Motivo: ${movimiento.motivo}")
        }
    }
}
