package cat.deim.asm_22.p2_patinfly.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Representa una bicicleta con sus propiedades principales.
 *
 * @property uuid Identificador único de la bicicleta.
 * @property name Nombre o denominación de la bicicleta.
 * @property bikeType Tipo de bicicleta, mapeado desde el campo JSON "bike_type".
 * @property creationDate Fecha de creación del registro de la bicicleta.
 * @property bikeTypeName Nombre del tipo de bicicleta, extraído del campo JSON "bike_type_name".
 * @property lastMaintenanceDate Fecha de la última revisión o mantenimiento, puede ser null si no hay registro.
 * @property inMaintenance Indica si la bicicleta está actualmente en mantenimiento.
 * @property isActive Indica si la bicicleta está activa y disponible.
 * @property isDeleted Indica si la bicicleta ha sido marcada como eliminada.
 * @property batteryLevel Nivel de batería actual, si aplica.
 * @property meters Distancia recorrida o registrada en metros.
 * @property isRented Indica si la bicicleta está actualmente alquilada.
 * @property lat Latitud de la ubicación actual de la bicicleta, puede ser null.
 * @property lon Longitud de la ubicación actual de la bicicleta, puede ser null.
 * @property isReserved Indica si la bicicleta está reservada para un usuario.
 * @property rentalUris URI(s) relacionados con el alquiler, puede ser null.
 * @property groupCourse Curso o grupo asociado a la bicicleta, puede ser null.
 */
data class Bike(
    val uuid: String,
    val name: String,

    @SerializedName("bike_type")
    val bikeType: BikeType,

    @SerializedName("creation_date")
    val creationDate: String,

    @SerializedName("bike_type_name")
    val bikeTypeName: String,

    @SerializedName("last_maintenance_date")
    val lastMaintenanceDate: String?,

    @SerializedName("in_maintenance")
    val inMaintenance: Boolean,

    @SerializedName("is_active")
    var isActive: Boolean,

    @SerializedName("is_deleted")
    val isDeleted: Boolean,

    @SerializedName("battery_level")
    val batteryLevel: Int,

    val meters: Int,

    val isRented: Boolean,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?,

    @SerializedName("is_reserved")
    val isReserved: Boolean,

    @SerializedName("rental_uris")
    val rentalUris: String?,

    @SerializedName("group_course")
    val groupCourse: String?
)
