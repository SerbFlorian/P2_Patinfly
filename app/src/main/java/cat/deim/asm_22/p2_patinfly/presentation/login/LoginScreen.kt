package cat.deim.asm_22.p2_patinfly.presentation.login

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.presentation.MainActivity

/**
 * Pantalla de Login para la aplicación Patinfly.
 * Se encarga de mostrar los campos de email y contraseña,
 * manejar la validación y mostrar el estado del login.
 *
 * @param viewModel ViewModel que maneja la lógica de negocio del login.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    // Estado UI y resultado de login obtenidos del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val loginResult by viewModel.loginResult.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(21.dp)
                .verticalScroll(rememberScrollState()), // Permite scroll vertical en pantalla pequeña
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeader()

            EmailField(
                email = uiState.email,
                isUserValid = uiState.isUserValid,
                onValueChange = viewModel::onEmailChanged
            )

            PasswordField(
                password = uiState.password,
                isPasswordValid = uiState.isPasswordValid,
                onValueChange = viewModel::onPasswordChanged,
                onLogin = { viewModel.login(uiState.email, uiState.password) }
            )

            ForgotPasswordLink()

            LoginButton(
                enabled = uiState.email.isNotEmpty() && uiState.password.isNotEmpty(),
                onClick = { viewModel.login(uiState.email, uiState.password) }
            )

            Spacer(modifier = Modifier.height(100.dp))

            // Manejo del estado del resultado del login
            when (loginResult) {
                is LoginViewModel.LoginResult.Loading -> CircularProgressIndicator()
                is LoginViewModel.LoginResult.Success -> {
                    // Si el login es exitoso, lanzar MainActivity
                    LaunchedEffect(Unit) {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
                is LoginViewModel.LoginResult.Error -> {
                    val errorMessage = (loginResult as LoginViewModel.LoginResult.Error).message
                    AlertDialog(
                        onDismissRequest = { viewModel._loginResult.value =
                            LoginViewModel.LoginResult.Idle
                        },
                        title = { Text("Error de Autenticación") },
                        text = { Text(errorMessage) },
                        confirmButton = {
                            Button(onClick = { viewModel._loginResult.value =
                                LoginViewModel.LoginResult.Idle
                            }) {
                                Text("Ok")
                            }
                        }
                    )
                }
                LoginViewModel.LoginResult.Idle -> {}
            }
        }
    }
}

/**
 * Encabezado de la pantalla de login con imagen y título.
 */
@Composable
private fun LoginHeader() {
    Image(
        painter = painterResource(id = R.drawable.login_image),
        contentDescription = "Logo de la aplicación",
        modifier = Modifier
            .size(400.dp)
            .padding(bottom = 16.dp, top = 30.dp)
    )
    Text(
        text = "Patinfly",
        fontSize = 60.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 30.dp)
    )
}

/**
 * Campo para introducir el email.
 *
 * @param email Valor actual del email.
 * @param isUserValid Boolean que indica si el email es válido.
 * @param onValueChange Lambda que se ejecuta al cambiar el texto.
 */
@Composable
private fun EmailField(
    email: String,
    isUserValid: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Email", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onValueChange,
            isError = email.isNotEmpty() && !isUserValid, // Mostrar error solo si hay texto y no es válido
            label = { Text(if (email.isNotEmpty() && !isUserValid) "Incorrect email" else "Enter your email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (email.isNotEmpty()) if (isUserValid) Color.Green else Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (email.isNotEmpty()) if (isUserValid) Color.Green else Color.Red else MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * Campo para introducir la contraseña.
 *
 * @param password Valor actual de la contraseña.
 * @param isPasswordValid Boolean que indica si la contraseña es válida.
 * @param onValueChange Lambda que se ejecuta al cambiar el texto.
 * @param onLogin Lambda que se ejecuta cuando se pulsa "Done" en el teclado.
 */
@Composable
private fun PasswordField(
    password: String,
    isPasswordValid: Boolean,
    onValueChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Password", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            isError = password.isNotEmpty() && !isPasswordValid,
            label = { Text(if (password.isNotEmpty() && !isPasswordValid) "Incorrect password" else "Enter your password") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onLogin() }), // Permite login desde el teclado
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto de la contraseña
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (password.isNotEmpty()) if (isPasswordValid) Color.Green else Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (password.isNotEmpty()) if (isPasswordValid) Color.Green else Color.Red else MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * Enlace para recuperación de contraseña.
 * Actualmente sin funcionalidad implementada.
 */
@Composable
private fun ForgotPasswordLink() {
    Text(
        text = "Forgot your password?",
        color = Color.Gray,
        modifier = Modifier
            .padding(top = 4.dp, bottom = 25.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
            .clickable {  },
        fontWeight = FontWeight.Bold
    )
}

/**
 * Botón para iniciar sesión.
 *
 * @param enabled Indica si el botón está habilitado (para evitar logins vacíos).
 * @param onClick Lambda que se ejecuta al pulsar el botón.
 */
@Composable
private fun LoginButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    if (enabled) Color.Green.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(64.dp),
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Acceder a la aplicación",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
