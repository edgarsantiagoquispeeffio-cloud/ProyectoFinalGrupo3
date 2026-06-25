package com.example.proyectofinalgrupo3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectofinalgrupo3.data.business.InventarioBusinessRules
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.data.model.InventarioKpi
import com.example.proyectofinalgrupo3.ui.viewmodel.StockDashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardStock(
    viewModel: StockDashboardViewModel,
    onBack: () -> Unit
) {
    val indicadores by viewModel.indicadores.collectAsState()
    val alertas by viewModel.productosStockBajo.collectAsState()
    val productos by viewModel.productosOrdenados.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Dashboard de Stock")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item(key = "kpis") {
                KpiSection(
                    indicadores = indicadores
                )
            }

            item(key = "alert-title") {
                Text(
                    text = "Alertas de reposición",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (alertas.isEmpty()) {
                item(key = "no-alerts") {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No hay productos con stock bajo.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(
                    items = alertas,
                    key = { producto ->
                        "alerta-${producto.idProducto}"
                    }
                ) { producto ->
                    ProductoDashboardRow(
                        producto = producto,
                        forceWarning = true
                    )
                }
            }

            item(key = "all-title") {
                Text(
                    text = "Estado general de productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                items = productos,
                key = { producto ->
                    "producto-${producto.idProducto}"
                }
            ) { producto ->
                ProductoDashboardRow(
                    producto = producto
                )
            }
        }
    }
}

@Composable
private fun KpiSection(
    indicadores: InventarioKpi
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Total de productos",
            value = indicadores.totalProductos.toString()
        )

        KpiCard(
            title = "Stock bajo",
            value = indicadores.productosStockBajo.toString()
        )

        KpiCard(
            title = "Valor estimado",
            value = NumberFormat
                .getCurrencyInstance(Locale("es", "PE"))
                .format(indicadores.valorEstimadoInventario)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProductoDashboardRow(
    producto: ProductoEntity,
    forceWarning: Boolean = false
) {
    val stockBajo = forceWarning || InventarioBusinessRules.esStockBajo(producto.stockActual)

    val precioFormateado = NumberFormat
        .getCurrencyInstance(Locale("es", "PE"))
        .format(producto.precio)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (stockBajo) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (stockBajo) {
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "Stock bajo",
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                                color = MaterialTheme.colorScheme.onError,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {
                    Text(
                        text = "Stock: ${producto.stockActual}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mín.: ${producto.stockMinimo}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = precioFormateado,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = "SKU: ${producto.sku}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Talla: ${producto.talla} | Color: ${producto.color}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}