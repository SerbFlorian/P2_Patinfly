package cat.deim.asm_22.p2_patinfly.presentation.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.presentation.detailBike.DetailBikeActivity

/**
 * Composable que muestra el contenido de la lista de bicicletas disponibles.
 *
 * Dependiendo de si la colección está vacía o no, muestra un mensaje indicando
 * que no hay bicicletas disponibles o una lista ordenada de tarjetas con detalles de cada bicicleta.
 *
 * @param bikes Colección de objetos [Bike] a mostrar.
 */
@Composable
fun BikeListScreen(bikes: Collection<Bike>) {
    // Si no hay bicicletas disponibles, muestra un mensaje centrado
    when {
        bikes.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay bicicletas disponibles",
                    fontFamily = FontFamily.SansSerif,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        else -> {
            // Mostrar la lista de bicicletas ordenadas por distancia ascendente (más cercana primero)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // items muestra cada bicicleta usando el composable BikeCard
                items(bikes.sortedBy { it.meters }) { bike ->
                    BikeCard(bike = bike)
                }
            }
        }
    }
}

/**
 * Composable que muestra una tarjeta con la información detallada de una bicicleta.
 *
 * Incluye:
 * - Imagen representativa (placeholder)
 * - Distancia en kilómetros (formateada a un decimal)
 * - Estado de la batería (alto, medio, bajo)
 * - Tipo de bicicleta
 * - Botón para navegar a la pantalla de detalles, pasando el ID de la bicicleta.
 *
 * @param bike Objeto [Bike] que contiene los datos que se mostrarán en la tarjeta.
 */
@SuppressLint("DefaultLocale")
@Composable
fun BikeCard(bike: Bike) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            // Imagen representativa de la bicicleta (placeholder splash_image)
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = "Imagen de la bicicleta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(21.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Distancia convertida a kilómetros con un decimal
            val distanceKm = String.format("%.1f", bike.meters / 1000.0)
            Text(
                text = "$distanceKm km away",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    // Determina el estado de la batería según nivel
                    val batteryStatus = when {
                        bike.batteryLevel > 50 -> "High"
                        bike.batteryLevel == 50 -> "Medium"
                        else -> "Low"
                    }
                    Text(text = batteryStatus, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "battery",
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // Mostrar el tipo de bicicleta (Electric, Urban, Gas, etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = bike.bikeTypeName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Botón para navegar a los detalles de la bicicleta, pasando su UUID
            Button(
                onClick = {
                    val intent = Intent(context, DetailBikeActivity::class.java).apply {
                        putExtra("bikeId", bike.uuid)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5fff33))
            ) {
                Text(
                    text = "View Details",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
