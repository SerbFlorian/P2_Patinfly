package cat.deim.asm_22.p2_patinfly.presentation.detailBike

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.BikeTypeModel
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable que muestra la pantalla de detalle de una bicicleta.
 *
 * Gestiona el estado de reserva de la bicicleta y renderiza la tarjeta
 * de detalle con la información relevante.
 *
 * @param bike Objeto Bike que contiene los datos de la bicicleta a mostrar.
 * @param viewModel ViewModel para manejar la lógica de negocio y acciones.
 */
@Composable
@SuppressLint("DefaultLocale")
fun DetailBikeScreen(bike: Bike, viewModel: DetailBikeViewModel) {
    // isReserved indica si la bicicleta está reservada, se inicializa con bike.isActive
    var isReserved by remember { mutableStateOf(bike.isActive) }

    val coroutineScope = rememberCoroutineScope()

    val buttonText = if (isReserved) "Reserved" else "Reserve now"
    val buttonColor = if (isReserved) Color.LightGray else Color(0xFF5FFF33)

    Column(modifier = Modifier.padding(16.dp)) {
        BikeDetailCard(
            imageRes = R.drawable.splash_image,
            bikeType = BikeTypeModel.fromDomain(bike.bikeType),
            isAvailable = !isReserved,
            modifier = Modifier.fillMaxWidth(),
            bike = bike,
            buttonText = buttonText,
            buttonColor = buttonColor,
            onReserveClick = {
                coroutineScope.launch {
                    viewModel.toggleReservation(bike)
                    // Asumimos que ViewModel actualiza bike.isActive a 1 al reservar
                    isReserved = true
                }
            }
        )
    }
}

/**
 * Composable que representa una tarjeta con los detalles de una bicicleta,
 * mostrando imagen, tipo, disponibilidad, botón de reserva y detalles adicionales.
 *
 * @param bike Objeto Bike con los datos de la bicicleta.
 * @param imageRes ID del recurso de imagen para mostrar.
 * @param bikeType Tipo de bicicleta en modelo local.
 * @param buttonText Texto que se muestra en el botón (ej. "Reserve now" o "Reserved").
 * @param buttonColor Color del botón según estado de reserva.
 * @param isAvailable Booleano que indica si la bicicleta está disponible para reserva.
 * @param modifier Modifier para aplicar al Card.
 * @param onReserveClick Lambda que se ejecuta al pulsar el botón de reserva.
 */
@Composable
fun BikeDetailCard(
    bike: Bike,
    imageRes: Int,
    bikeType: BikeTypeModel,
    buttonText: String,
    buttonColor: Color,
    isAvailable: Boolean,
    modifier: Modifier,
    onReserveClick: () -> Unit
) {
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = try {
        val dateTime = LocalDateTime.parse(bike.creationDate)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        bike.creationDate // fallback si no se puede parsear la fecha
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {

            // Fila superior con imagen, disponibilidad, tipo y botón de reserva
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Bike Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

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
                        text = bikeType.uuid,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = onReserveClick,
                    enabled = isAvailable,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(text = buttonText)
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // Fila con iconos y textos informativos: batería, distancia, fecha creación
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow(
                    icon = Icons.Default.BatteryFull,
                    text = "Battery: ${bike.batteryLevel}%"
                )
                InfoRow(
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    text = "Distance: ${bike.meters} meters"
                )
                InfoRow(
                    icon = Icons.Default.Person,
                    text = "Created on: $formattedDate"
                )
            }
        }
    }
}

/**
 * Composable que muestra una fila con un icono y un texto descriptivo.
 *
 * @param icon Icono a mostrar (ImageVector).
 * @param text Texto descriptivo que acompaña al icono.
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
