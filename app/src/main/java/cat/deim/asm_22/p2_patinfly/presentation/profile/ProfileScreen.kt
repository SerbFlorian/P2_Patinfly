package cat.deim.asm_22.p2_patinfly.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.UserModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable principal que muestra la pantalla del perfil del usuario.
 *
 * Observa el estado de la UI desde el ViewModel y muestra el contenido adecuado:
 * - Loading mientras se obtienen datos
 * - Perfil y historial cuando la carga es exitosa
 * - Mensaje de error si algo falla
 *
 * @param viewModel Instancia de [ProfileViewModel], se crea por defecto con viewModel()
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> LoadingProfile()
            is ProfileUiState.Success -> {
                UserProfileCard(user = state.user)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Rental History",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                RentalHistorySection(
                    bikes = state.user?.rentalHistory ?: emptyList(),
                    viewModel = viewModel
                )
            }
            is ProfileUiState.Error -> ErrorProfile(message = state.message)
        }
    }
}

/**
 * Sección que muestra el historial de alquiler de bicicletas.
 * Si no hay bicicletas, muestra un mensaje indicando ausencia de historial.
 *
 * @param bikes Lista de bicicletas alquiladas [BikeDTO]
 * @param viewModel Instancia del ViewModel para interacción (actualización, eliminación)
 */
@Composable
fun RentalHistorySection(
    bikes: List<BikeDTO>,
    viewModel: ProfileViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        if (bikes.isEmpty()) {
            Text(
                text = "No rental history available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            // Para cada bicicleta, mostrar una tarjeta con detalles
            bikes.forEach { bike ->
                BikeCard(
                    bike = bike,
                    viewModel = viewModel,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Composable que muestra los detalles de una bicicleta dentro de una tarjeta.
 * Al hacer clic, expande o contrae para mostrar más detalles y una acción para borrar.
 *
 * @param bike Datos de la bicicleta a mostrar [BikeDTO]
 * @param viewModel ViewModel para operaciones (actualizar estado, eliminar)
 * @param modifier Modifier para personalizar el layout
 */
@Composable
fun BikeCard(
    bike: BikeDTO,
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Intenta formatear la fecha, si falla muestra la fecha original
    val formattedDate = try {
        val dateTime = LocalDateTime.parse(bike.creationDate)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        bike.creationDate
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }, // Toggle expand/collapse
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bike.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f)) // Empuja el texto al extremo derecho
                Text(
                    text = bike.bikeTypeName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow(
                        Icons.Default.BatteryFull,
                        "Battery: ${bike.batteryLevel}%"
                    )
                    InfoRow(
                        Icons.AutoMirrored.Filled.DirectionsBike,
                        "Distance: ${bike.meters} meters"
                    )
                    InfoRow(
                        Icons.Default.Person,
                        formattedDate
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Marca la bici como inactiva y elimina de historial en ViewModel
                            val updatedBike = bike.copy(isActive = false)
                            viewModel.updateBikeStatus(updatedBike)
                            viewModel.removeBikeFromHistory(bike.uuid)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text(
                            text = "Delete reservation",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila simple que muestra un icono y un texto alineados horizontalmente.
 *
 * @param icon Icono a mostrar [ImageVector]
 * @param text Texto descriptivo asociado al icono
 */
@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Tarjeta que muestra la información básica del perfil del usuario:
 * Imagen, nombre, email, fecha de creación y última conexión.
 *
 * @param user Modelo del usuario [UserModel]
 */
@Composable
private fun UserProfileCard(user: UserModel?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageSection()
            Spacer(modifier = Modifier.width(16.dp))
            UserDetailsSection(user = user)
        }
    }
}

/**
 * Sección para mostrar la imagen de perfil y un texto estático "Profile".
 */
@Composable
private fun ProfileImageSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Profile",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

/**
 * Sección que muestra detalles textuales del usuario con formateo de fechas.
 *
 * @param user Modelo del usuario [UserModel]
 */
@Composable
private fun UserDetailsSection(user: UserModel?) {
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Intentamos formatear las fechas de creación y última conexión
    val formattedCreationDate = try {
        val dateTime = LocalDateTime.parse(user?.creationDate)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        user?.creationDate?.substringBefore('T') ?: "N/A"
    }

    val formattedLastConnection = try {
        val dateTime = LocalDateTime.parse(user?.lastConnection)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        user?.lastConnection?.substringBefore('T') ?: "N/A"
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (user != null) {
            ProfileDetailItem(label = "Name", value = user.name, isHeader = true)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Email", value = user.email)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Date of creation", value = formattedCreationDate)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Last connection", value = formattedLastConnection)
        }
    }
}

/**
 * Texto que muestra una etiqueta y un valor para un dato del perfil.
 *
 * @param label Nombre o descripción del campo
 * @param value Valor asociado
 * @param isHeader Indica si el texto debe mostrarse con estilo destacado
 */
@Composable
private fun ProfileDetailItem(label: String, value: String, isHeader: Boolean = false) {
    Text(
        text = "$label: $value",
        style = if (isHeader) {
            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        } else {
            MaterialTheme.typography.bodyMedium
        }
    )
}

/**
 * Muestra un Divider horizontal sencillo con color gris claro.
 *
 * @param modifier Modifier para personalizar el layout
 */
@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
        color = Color.LightGray
    )
}

/**
 * Composable que muestra una pantalla de carga con un texto.
 */
@Composable
fun LoadingProfile() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading profile...", style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Composable que muestra un mensaje de error.
 *
 * @param message Mensaje de error a mostrar
 */
@Composable
fun ErrorProfile(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Error loading profile: $message", color = Color.Red)
    }
}
