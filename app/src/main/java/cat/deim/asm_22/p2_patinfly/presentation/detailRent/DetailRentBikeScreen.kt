package cat.deim.asm_22.p2_patinfly.presentation.detailRent

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Pantalla principal para mostrar el detalle del alquiler de una bicicleta.
 *
 * Muestra distintos estados UI: Loading, Error, Success. En el estado Success, despliega
 * los detalles de la bicicleta activa, o mensajes si no hay bicicleta activa o no está activa.
 *
 * @param uiState Estado actual de la UI que determina qué mostrar.
 * @param bikeViewModel ViewModel que maneja la lógica para cargar y actualizar el estado de la bicicleta.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DetailRentBikeScreen(
    uiState: DetailRentBikeUiState,
    bikeViewModel: DetailRentBikeViewModel = viewModel()
) {
    // Carga inicial de la primera bicicleta activa al lanzar la composición
    LaunchedEffect(Unit) {
        bikeViewModel.loadFirstActiveBike()
    }

    when (uiState) {
        is DetailRentBikeUiState.Loading -> {
            // Indicador de carga centrado
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is DetailRentBikeUiState.Error -> {
            // Mensaje de error centrado con estilo de error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is DetailRentBikeUiState.Success -> {
            when {
                uiState.bike == null -> {
                    // No hay bicicleta reservada, se informa al usuario
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You need to reserve a bike first",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
                !uiState.bike.isActive -> {
                    // Bicicleta no está activa, se recomienda reservar otra
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "This bike is not active. Please reserve another one.",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
                else -> {
                    // Bicicleta activa, se muestra detalle
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.bike.bikeTypeName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        BikeDetailCard(
                            bike = uiState.bike,
                            imageRes = R.drawable.splash_image,
                            // Conversión de modelo
                            isAvailable = uiState.bike.isActive,
                            modifier = Modifier.fillMaxWidth(),
                            onRentClick = { isRented ->
                                // Actualiza el estado de alquiler en ViewModel
                                val updatedBike = uiState.bike.copy(isRented = isRented)
                                bikeViewModel.updateBikeRentStatus(updatedBike)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable que muestra los detalles de una bicicleta en una tarjeta.
 *
 * Incluye imagen, estado (disponible o no), nombre, botón para alquilar o parar alquiler,
 * y detalles adicionales como batería, distancia y fecha de creación.
 *
 * @param bike Objeto Bike con información a mostrar.
 * @param imageRes Recurso de imagen a mostrar.
 * @param isAvailable Indica si la bicicleta está disponible para alquiler.
 * @param modifier Modificador para personalizar el layout.
 * @param onRentClick Callback que notifica cuando se quiere cambiar el estado de alquiler.
 */
@SuppressLint("DefaultLocale")
@Composable
fun BikeDetailCard(
    bike: Bike,
    imageRes: Int,
    isAvailable: Boolean,
    modifier: Modifier,
    onRentClick: (Boolean) -> Unit
) {
    // Formateo de la fecha de creación a yyyy-MM-dd, con manejo de error
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = try {
        val dateTime = LocalDateTime.parse(bike.creationDate)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        // En caso de error en formato, mostrar texto original
        bike.creationDate
    }

    // Estado local para mostrar diálogo de confirmación y estado pendiente de alquiler
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var pendingRentStatus by remember { mutableStateOf(bike.isRented) }

    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen circular de la bicicleta
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Bike Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Columna con estado de disponibilidad y nombre
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isAvailable) "Available" else "Not Available",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Text(
                        text = bike.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para alquilar o parar alquiler, con cambio de color según estado
                Button(
                    onClick = {
                        // Cambia el estado de alquiler y muestra diálogo (si se implementa)
                        onRentClick(pendingRentStatus)
                        pendingRentStatus = !bike.isRented
                        showConfirmationDialog = true
                    },
                    enabled = isAvailable,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bike.isRented) Color(0xFFF44336) else Color(0xFF5FFF33),
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    Text(
                        text = if (bike.isRented) "Stop rent" else "Rent",
                        color = if (bike.isRented) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // Información adicional de la bicicleta con iconos descriptivos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow(Icons.Default.BatteryFull, "Battery: ${bike.batteryLevel}%")
                InfoRow(Icons.AutoMirrored.Filled.DirectionsBike, "Distance: ${bike.meters} meters")
                InfoRow(Icons.Default.Person, "Created on: $formattedDate")
            }
        }
    }
}

/**
 * Fila sencilla con icono y texto para mostrar información adicional.
 *
 * @param icon Icono que representa la información.
 * @param text Texto descriptivo.
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
