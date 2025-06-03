package cat.deim.asm_22.p2_patinfly.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.deim.asm_22.p2_patinfly.R
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.data.repository.UserRepository
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.User
import cat.deim.asm_22.p2_patinfly.presentation.profile.ProfileActivity
import cat.deim.asm_22.p2_patinfly.presentation.detailRent.DetailRentBikeActivity
import cat.deim.asm_22.p2_patinfly.presentation.screen.BikeListActivity
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme
import cat.deim.asm_22.p2_patinfly.presentation.detailBike.DetailBikeActivity

/**
 * Actividad principal de la aplicación Patinfly.
 * Sirve como punto de entrada principal y muestra la interfaz inicial con:
 * - Barra superior con título y botón de mapa.
 * - Tarjeta de perfil del usuario.
 * - Listado horizontal de bicicletas disponibles.
 * - Sección de categorías de bicicletas.
 * - Botón flotante para escanear QR.
 * Gestiona la carga inicial de datos del usuario y bicicletas, y proporciona
 * navegación a otras pantallas de la aplicación.
 */
class MainActivity : ComponentActivity() {
    /**
     * Metodo llamado al crear la actividad.
     * Inicializa los repositorios, configura el contenido Compose y
     * carga los datos iniciales del usuario y las bicicletas.
     *
     * @param savedInstanceState Estado previo de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de repositorios (fuente única de verdad)
        val userRepository = UserRepository.create(this)
        val bikeRepository = BikeRepository.create(this)

        setContent {
            PatinflyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estados para manejar los datos y carga
                    var user by remember { mutableStateOf<User?>(null) }
                    var bikes by remember { mutableStateOf<List<Bike>>(emptyList()) }
                    var isLoading by remember { mutableStateOf(true) }

                    // Efecto para carga inicial de datos (se ejecuta una vez)
                    LaunchedEffect(Unit) {
                        user = userRepository.getUser() // Obtener usuario actual
                        bikes = bikeRepository.getAll().toList().sortedBy { it.meters } // Bicis ordenadas por distancia
                        isLoading = false // Finalizar carga
                    }

                    // Mostrar contenido según estado de carga
                    if (!isLoading) {
                        // Obtener primera bicicleta para el botón QR
                        val bike = bikes.firstOrNull()

                        // Mostrar interfaz principal
                        MainScreenContent(
                            user = user,
                            bikes = bikes
                        )

                        // Mostrar botón QR solo si hay bicicletas
                        bike?.let { QRButton(bike = it) }
                    } else {
                        // Mostrar indicador de carga
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable que representa el contenido principal de la pantalla.
 * Organiza los elementos principales en una superficie que ocupa toda la pantalla.
 *
 * @param user Usuario actual, puede ser null.
 * @param bikes Lista de bicicletas disponibles.
 */
@Composable
private fun MainScreenContent(user: User?, bikes: List<Bike>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppTopBar() // Barra superior

                if (user != null) {
                    MainContent( // Contenido principal
                        user = user,
                        bikes = bikes
                    )
                } else {
                    // Mostrar carga si no hay usuario
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

/**
 * Composable que representa la barra superior de la aplicación.
 * Contiene el título de la aplicación y un botón de mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    TopAppBar(
        title = { AppTitle() },
        actions = { MapIcon() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        ),
        modifier = Modifier.shadow(4.dp) // Sombra para efecto elevación
    )
}

/**
 * Composable que muestra el título de la aplicación en la barra superior.
 * Muestra el texto "Patinfly" centrado con padding ajustado para compensar el botón de acción.
 */
@Composable
private fun AppTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 46.dp) // Compensar botón de acción
                .weight(1f)
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                style = MaterialTheme.typography.displayLarge,
                text = "Patinfly",
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Composable que muestra el botón de mapa en la barra superior.
 * Actualmente es un botón decorativo sin funcionalidad implementada.
 */
@Composable
private fun MapIcon() {
    Box(modifier = Modifier.padding(end = 16.dp)) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            color = Color.White
        ) {
            IconButton(
                onClick = {  },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Filled.Map, "Mapa", tint = Color.Black)
            }
        }
    }
}

/**
 * Composable que organiza el contenido principal de la pantalla en una columna vertical.
 * Incluye tarjeta de perfil, sección "Around you", lista de bicicletas y categorías.
 *
 * @param user Usuario actual, puede ser null.
 * @param bikes Lista de bicicletas navegables.
 */
@Composable
private fun MainContent(user: User?, bikes: List<Bike>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileCard(user, context) // Tarjeta de perfil
        AroundYouSection(context) // Sección "Around you"
        BikeCardsHorizontal(bikes, context) // Lista horizontal de bicis
        CategoriesSection(context = context) // Categorías
    }
}

/**
 * Composable que muestra la tarjeta de perfil del usuario.
 * Incluye la imagen de perfil y el nombre del usuario, con navegación a la pantalla de perfil al hacer clic.
 *
 * @param user Usuario a mostrar, puede ser null.
 * @param context Contexto de Android para navegación.
 */
@Composable
private fun ProfileCard(user: User?, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navegar a perfil pasando categorías como parámetro
                navigateTo<ProfileActivity>(context, listOf("urban", "electric"))
            },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage() // Imagen de perfil
            Spacer(modifier = Modifier.width(22.dp))

            // Mostrar nombre si existe usuario
            user?.let {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable que muestra la imagen de perfil del usuario.
 * Actualmente utiliza una imagen estática (splash_image).
 */
@Composable
private fun ProfileImage() {
    Image(
        painter = painterResource(id = R.drawable.splash_image),
        contentDescription = "Imagen de perfil",
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

/**
 * Composable que muestra la sección "Around you" con un botón para ver todas las bicicletas.
 *
 * @param context Contexto de Android para navegación.
 */
@Composable
private fun AroundYouSection(context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Around you",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable {
                // Navegar a lista de bicis con todas las categorías
                navigateTo<BikeListActivity>(context, listOf("urban", "electric","gas"))
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                navigateTo<BikeListActivity>(context, listOf("urban", "electric","gas"))
            },
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver todas las bicicletas",
                tint = Color.Black
            )
        }
    }
}

/**
 * Función genérica para navegar entre actividades.
 * Pasa una lista de categorías como parámetro extra en el Intent.
 *
 * @param T Tipo de la actividad destino.
 * @param context Contexto de Android.
 * @param categories Lista de categorías a pasar como parámetro.
 */
inline fun <reified T> navigateTo(context: Context, categories: List<String>) {
    val intent = Intent(context, T::class.java).apply {
        putStringArrayListExtra("categories", ArrayList(categories))
    }
    context.startActivity(intent)
}

/**
 * Composable que muestra una lista horizontal de tarjetas de bicicletas.
 *
 * @param bikes Lista de bicicletas a mostrar.
 * @param context Contexto de Android para navegación.
 */
@Composable
private fun BikeCardsHorizontal(bikes: List<Bike>, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        bikes.forEach { bike ->
            BikeCard(bike, context) // Tarjeta individual
        }
    }
}

/**
 * Composable que muestra una tarjeta individual de bicicleta.
 * Incluye imagen y distancia, con navegación a la pantalla de detalle al hacer clic.
 *
 * @param bike Bicicleta a mostrar.
 * @param context Contexto de Android para navegación.
 */
@Composable
private fun BikeCard(bike: Bike, context: Context) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .clickable {
                // Navegar a detalle de bicicleta
                val intent = Intent(context, DetailBikeActivity::class.java).apply {
                    putExtra("bikeId", bike.uuid) // Pasar ID como parámetro
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = "Imagen de la bicicleta",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "${bike.meters} meters away",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

/**
 * Composable que muestra la sección de categorías de bicicletas.
 * Incluye botones para filtrar por categorías "Urban" y "Electric".
 *
 * @param context Contexto de Android para navegación.
 */
@Composable
private fun CategoriesSection(context: Context) {
    // Estado para categoría seleccionada (no implementado completamente)
    val selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(top = 32.dp)) {
        SectionTitle() // Título de sección

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Mostrar categoría Urban
            if (selectedCategory == null || selectedCategory == "urban") {
                BikeCategory(
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    label = "Urban",
                    onClick = {
                        val intent = Intent(context, BikeListActivity::class.java)
                        intent.putExtra("category", "urban")
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Mostrar categoría Electric
            if (selectedCategory == null || selectedCategory == "electric") {
                BikeCategory(
                    icon = Icons.Default.ElectricBike,
                    label = "Electric",
                    onClick = {
                        val intent = Intent(context, BikeListActivity::class.java)
                        intent.putExtra("category", "electric")
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * Composable que muestra el título de una sección genérica.
 */
@Composable
private fun SectionTitle() {
    Text(
        text = "Categories",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        textAlign = TextAlign.Start
    )
}

/**
 * Composable que muestra una categoría individual de bicicleta.
 * Incluye un icono y una etiqueta, con acción al hacer clic.
 *
 * @param icon Icono representativo de la categoría.
 * @param label Texto descriptivo de la categoría.
 * @param onClick Callback ejecutado al hacer clic.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
private fun BikeCategory(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable { onClick() },
            color = Color.White
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(bottom = 8.dp),
                    tint = Color.Black
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/**
 * Composable que muestra un botón flotante para escanear un código QR.
 * Posicionado en la esquina inferior derecha, navega a la pantalla de alquiler al hacer clic.
 *
 * @param bike Bicicleta asociada al código QR.
 */
@Composable
private fun QRButton(bike: Bike) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .clickable {
                    val intent = Intent(context, DetailRentBikeActivity::class.java).apply {
                        putExtra("bikeId", bike.uuid) // Pasar ID de bicicleta
                    }
                    context.startActivity(intent)
                },
            color = Color(0xff5fff33) // Verde brillante
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Escanear QR",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }
        }
    }
}