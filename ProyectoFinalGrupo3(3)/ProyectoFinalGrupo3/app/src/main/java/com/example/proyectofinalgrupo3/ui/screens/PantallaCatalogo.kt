package com.example.proyectofinalgrupo3.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import com.example.proyectofinalgrupo3.ui.viewmodel.CatalogoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    viewModel: CatalogoViewModel,
    onBack: (() -> Unit)? = null,
    onVerDetalle: (Int) -> Unit,
    onVerCarrito: () -> Unit
) {
    val productos by viewModel.productosFiltrados.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaFiltro.collectAsState()
    val tallaSeleccionada by viewModel.tallaFiltro.collectAsState()

    val colorPrincipal = Color(0xFF6200EE)
    val colorFondo = Color(0xFFFBFBFB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Catálogo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = onVerCarrito) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ver carrito",
                                tint = colorPrincipal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = colorFondo
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Sección de Filtros Estilizada
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Filtrar por:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var expandedCategoria by remember { mutableStateOf(false) }

                    // Chip de Categoría
                    Surface(
                        onClick = { expandedCategoria = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (categoriaSeleccionada != null) colorPrincipal.copy(alpha = 0.1f) else Color(0xFFF1F1F1),
                        border = if (categoriaSeleccionada != null) BorderStroke(1.dp, colorPrincipal) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (categoriaSeleccionada) {
                                    1 -> "Ropa"
                                    2 -> "Calzado"
                                    else -> "Categoría"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (categoriaSeleccionada != null) colorPrincipal else Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (categoriaSeleccionada != null) colorPrincipal else Color.Gray
                            )
                        }

                        DropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            DropdownMenuItem(text = { Text("Todas") }, onClick = { viewModel.actualizarCategoria(null); expandedCategoria = false })
                            DropdownMenuItem(text = { Text("Ropa") }, onClick = { viewModel.actualizarCategoria(1); expandedCategoria = false })
                            DropdownMenuItem(text = { Text("Calzado") }, onClick = { viewModel.actualizarCategoria(2); expandedCategoria = false })
                        }
                    }

                    // Botón Limpiar
                    if (categoriaSeleccionada != null || tallaSeleccionada != null) {
                        TextButton(onClick = { viewModel.limpiarFiltros() }) {
                            Text("Limpiar", color = Color.Red, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila de Tallas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("S", "M", "L", "XL").forEach { talla ->
                        val isSelected = tallaSeleccionada == talla
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) colorPrincipal else Color(0xFFF1F1F1))
                                .clickable {
                                    viewModel.actualizarTalla(if (isSelected) null else talla)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = talla,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de Productos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = productos,
                    key = { it.idProducto }
                ) { producto ->
                    ItemProductoCard(
                        producto = producto,
                        onClick = { onVerDetalle(producto.idProducto) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemProductoCard(
    producto: ProductoEntity,
    onClick: () -> Unit
) {
    val esStockBajo = producto.stockActual <= producto.stockMinimo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Placeholder de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF0F0F0), Color(0xFFE0E0E0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (producto.idCategoria == 2) Icons.Default.Star else Icons.Default.Face, 
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.LightGray
                )
                
                // Badge de Stock Bajo
                if (esStockBajo) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "¡Stock Bajo!",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${producto.talla} • ${producto.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${String.format(Locale.getDefault(), "%.2f", producto.precio)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color(0xFF6200EE).copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir",
                            modifier = Modifier.padding(6.dp),
                            tint = Color(0xFF6200EE)
                        )
                    }
                }
            }
        }
    }
}
