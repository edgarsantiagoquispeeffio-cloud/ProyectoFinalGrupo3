package com.example.proyectofinalgrupo3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyectofinalgrupo3.ui.viewmodel.LoginState
import com.example.proyectofinalgrupo3.ui.viewmodel.LoginViewModel

@Composable
fun PantallaLogin(
    viewModel: LoginViewModel,
    onLoginSuccess: (com.example.proyectofinalgrupo3.data.entities.UsuarioEntity) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Aquí agregué esta variable para controlar primero si quiero ingresar como administrador o como cliente.
    // Esto solo afecta la parte visual de la pantalla, no modifica la lógica del login.
    var tipoLoginSeleccionado by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    // Aquí definí los colores que usaré para mejorar el diseño del login.
    // El fondo será color piel, el botón de administrador gris y el botón de cliente celeste claro.
    val colorFondoPiel = Color(0xFFF6E3D4)
    val colorTarjeta = Color(0xFFFFF7F0)
    val colorBotonAdmin = Color(0xFF8E8E8E)
    val colorBotonCliente = Color(0xFFB3E5FC)

    // Cambié el contenedor principal a un Box para poder centrar mejor la tarjeta del login.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondoPiel)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        // Agregué una tarjeta central para que el login se vea más ordenado y moderno.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorTarjeta
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Si todavía no selecciono el tipo de usuario, muestro primero las dos opciones de ingreso.
                if (tipoLoginSeleccionado == null) {

                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Selecciona cómo deseas iniciar sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Agregué el botón para ingresar como administrador.
                    Button(
                        onClick = {
                            tipoLoginSeleccionado = "admin"
                            correo = ""
                            contrasena = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorBotonAdmin,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Administrador"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Ingresar como Administrador")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Agregué el botón para ingresar como cliente.
                    Button(
                        onClick = {
                            tipoLoginSeleccionado = "cliente"
                            correo = ""
                            contrasena = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorBotonCliente,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Cliente"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Ingresar como Cliente")
                    }

                } else {

                    // Cuando ya selecciono administrador o cliente, muestro el mismo formulario de login que ya tenía.
                    Text(
                        text = "Inicio de Sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Agregué este texto para indicar si estoy ingresando como administrador o cliente.
                    Text(
                        text = if (tipoLoginSeleccionado == "admin") {
                            "Acceso para administrador"
                        } else {
                            "Acceso para cliente"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Agregué esta opción para poder regresar y cambiar entre administrador o cliente.
                    TextButton(
                        onClick = {
                            tipoLoginSeleccionado = null
                            correo = ""
                            contrasena = ""
                        }
                    ) {
                        Text("Cambiar tipo de usuario")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mantengo la misma lógica de login que ya tenía.
                    // Solo cambié el color del botón según el tipo de usuario seleccionado.
                    Button(
                        onClick = { viewModel.login(correo, contrasena) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = uiState !is LoginState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipoLoginSeleccionado == "admin") {
                                colorBotonAdmin
                            } else {
                                colorBotonCliente
                            },
                            contentColor = if (tipoLoginSeleccionado == "admin") {
                                Color.White
                            } else {
                                Color.Black
                            }
                        )
                    ) {
                        Text("Iniciar Sesión")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mantengo igual el manejo de estados del login: carga, error y éxito.
                    when (uiState) {
                        is LoginState.Loading -> CircularProgressIndicator()

                        is LoginState.Error -> Text(
                            text = (uiState as LoginState.Error).mensaje,
                            color = MaterialTheme.colorScheme.error
                        )

                        is LoginState.Success -> {
                            val usuario = (uiState as LoginState.Success).usuario
                            LaunchedEffect(usuario) {
                                onLoginSuccess(usuario)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}