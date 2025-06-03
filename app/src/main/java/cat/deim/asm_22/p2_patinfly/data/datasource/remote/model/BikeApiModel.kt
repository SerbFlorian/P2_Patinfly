package cat.deim.asm_22.p2_patinfly.data.datasource.remote.model

import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.BikeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

/**
 * Modelo de datos que representa una bicicleta obtenida desde la API remota.
 * Utiliza serialización para mapear los campos JSON a propiedades Kotlin.
 *
 * @property id Identificador único de la bicicleta.
 * @property name Nombre de la bicicleta.
 * @property bikeTypeId Identificador del tipo de bicicleta.
 * @property groupCourse Grupo o curso asociado a la bicicleta (opcional).
 * @property lat Latitud de la ubicación actual de la bicicleta.
 * @property lon Longitud de la ubicación actual de la bicicleta.
 * @property meters Distancia recorrida en metros (valor aleatorio por defecto).
 * @property lastMaintenanceDate Fecha del último mantenimiento en formato ISO 8601 (opcional).
 * @property batteryLevel Nivel de batería de la bicicleta (valor aleatorio por defecto).
 * @property isDeleted Indica si la bicicleta está eliminada.
 * @property isActive Indica si la bicicleta está activa (valor aleatorio por defecto).
 * @property isDisabled Indica si la bicicleta está deshabilitada.
 * @property isReserved Indica si la bicicleta está reservada.
 * @property isRented Indica si la bicicleta está actualmente alquilada.
 * @property rentalUris URIs para la aplicación de alquiler en Android e iOS.
 * @property lastReported Última fecha/hora en que la bicicleta reportó su estado (opcional).
 */
@Serializable
data class BikeApiModel(
    @SerialName("vehicle_id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("vehicle_type_id") val bikeTypeId: String,
    @SerialName("group_course") val groupCourse: String?,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("meters") val meters: Int = (300..3000).random(),
    @SerialName("lastMaintenanceDate") val lastMaintenanceDate: String? = generateRandomDate(),
    @SerialName("batteryLevel") val batteryLevel: Int = (20..100).random(),
    @SerialName("isDeleted") val isDeleted: Boolean = false,
    @SerialName("is_activated") val isActive: Boolean = (0..1).random() == 1,
    @SerialName("is_disabled") val isDisabled: Boolean,
    @SerialName("is_reserved") val isReserved: Boolean,
    @SerialName("is_rented") val isRented: Boolean,
    @SerialName("rental_uris") val rentalUris: RentalUris = RentalUris("", ""),
    @SerialName("last_reported") val lastReported: String?
) {

    /**
     * Modelo de datos para las URIs de las aplicaciones móviles de alquiler.
     *
     * @property android URI para la app Android.
     * @property ios URI para la app iOS.
     */
    @Serializable
    data class RentalUris(
        @SerialName("android") val android: String,
        @SerialName("ios") val ios: String
    )

    // Nombre legible para el tipo de bicicleta derivado del ID del tipo
    private val bikeTypeName = when {
        bikeTypeId.startsWith("EB") -> "Electric"
        bikeTypeId.startsWith("RB") -> "Urban"
        bikeTypeId.startsWith("SCOOTER") -> "Gas"
        else -> "Unknown"
    }

    /**
     * Convierte el modelo API a un objeto del dominio [Bike].
     *
     * @return Una instancia de [Bike] con los datos convertidos.
     */
    fun toDomain(): Bike {
        val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        return Bike(
            uuid = id,
            name = name,
            bikeType = BikeType(uuid = bikeTypeId, name = bikeTypeName, type = bikeTypeId),
            bikeTypeName = bikeTypeName,
            creationDate = currentDate,
            lastMaintenanceDate = lastMaintenanceDate,
            inMaintenance = isDisabled,
            isActive = isActive,
            isDeleted = isDeleted,
            batteryLevel = batteryLevel,
            meters = meters,
            isRented = isRented,
            lat = lat,
            lon = lon,
            isReserved = isReserved,
            rentalUris = "Android: ${rentalUris.android}, iOS: ${rentalUris.ios}",
            groupCourse = groupCourse ?: ""
        )
    }
}

/**
 * Genera una fecha aleatoria en formato ISO-8601 entre el 1 de enero de 2023 y el 31 de diciembre de 2025.
 *
 * @return Fecha aleatoria como cadena en formato yyyy-MM-dd.
 */
fun generateRandomDate(): String {
    val startEpoch = LocalDate.of(2023, 1, 1).toEpochDay()
    val endEpoch = LocalDate.of(2025, 12, 31).toEpochDay()
    val randomEpoch = (startEpoch..endEpoch).random()
    return LocalDate.ofEpochDay(randomEpoch).toString()
}
