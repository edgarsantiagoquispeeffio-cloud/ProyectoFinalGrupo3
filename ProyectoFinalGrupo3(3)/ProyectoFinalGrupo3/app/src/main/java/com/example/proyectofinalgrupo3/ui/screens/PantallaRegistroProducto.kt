package com.example.proyectofinalgrupo3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinalgrupo3.ui.viewmodel.RegistroProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroProducto(
    viewModel: RegistroProductoViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RegistroProductoViewModel.UiEvent.Success -> {
                    Toast.makeText(context, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                }
                is RegistroProductoViewModel.UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Producto") },
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
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.sku,
                onValueChange = { viewModel.sku = it },
                label = { Text("SKU *") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.showErrors && viewModel.sku.isBlank()
            )

            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre del Producto *") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.showErrors && viewModel.nombre.isBlank()
            )

            // Selector de Categoría (Simulado)
            DropdownSelector(
                label = "Categoría *",
                options = listOf("1" to "Ropa", "2" to "Calzado", "3" to "Accesorios"),
                selectedOption = viewModel.idCategoria,
                onOptionSelected = { viewModel.idCategoria = it },
                isError = viewModel.showErrors && viewModel.idCategoria.isBlank()
            )

            // Selector de Colección (Opcional)
            DropdownSelector(
                label = "Colección",
                options = listOf("" to "Ninguna", "1" to "Verano 2024", "2" to "Edición Limitada"),
                selectedOption = viewModel.idColeccion,
                onOptionSelected = { viewModel.idColeccion = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.talla,
                    onValueChange = { viewModel.talla = it },
                    label = { Text("Talla *") },
                    modifier = Modifier.weight(1f),
                    isError = viewModel.showErrors && viewModel.talla.isBlank()
                )
                OutlinedTextField(
                    value = viewModel.color,
                    onValueChange = { viewModel.color = it },
                    label = { Text("Color *") },
                    modifier = Modifier.weight(1f),
                    isError = viewModel.showErrors && viewModel.color.isBlank()
                )
            }

            OutlinedTextField(
                value = viewModel.precio,
                onValueChange = { viewModel.precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.stockActual,
                    onValueChange = { viewModel.stockActual = it },
                    label = { Text("Stock Actual") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = viewModel.stockMinimo,
                    onValueChange = { viewModel.stockMinimo = it },
                    label = { Text("Stock Mínimo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            if (viewModel.showErrors) {
                Text(
                    text = "* Por favor completa los campos obligatorios",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.registrarProducto() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Guardar Producto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<Pair<String, String>>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = options.find { it.first == selectedOption }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            isError = isError
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onOptionSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}
