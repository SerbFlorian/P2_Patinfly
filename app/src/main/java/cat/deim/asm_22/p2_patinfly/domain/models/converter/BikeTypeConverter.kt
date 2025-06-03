package cat.deim.asm_22.p2_patinfly.domain.models.converter

import androidx.room.TypeConverter
import cat.deim.asm_22.p2_patinfly.domain.models.BikeType

class BikeTypeConverter {

    /**
     * Convierte un objeto [BikeType] a su representación en [String].
     *
     * Este metodo es utilizado por Room para almacenar el objeto [BikeType] en la base de datos
     * como un String, usando el campo `uuid` como identificador único.
     *
     * @param bikeType El objeto [BikeType] que se desea convertir a String. Puede ser null.
     * @return El UUID del [BikeType] como [String], o null si el parámetro es null.
     */
    @TypeConverter
    fun fromBikeType(bikeType: BikeType?): String? {
        return bikeType?.uuid
    }

    /**
     * Convierte un [String] que representa el UUID de un [BikeType] a un objeto [BikeType].
     *
     * Este metodo es utilizado por Room para reconstruir el objeto [BikeType] a partir de su
     * representación almacenada en la base de datos.
     *
     * Nota: Actualmente este metodo crea un objeto [BikeType] con valores de nombre y tipo
     * por defecto, por lo que deberías implementar aquí la lógica adecuada para recuperar
     * la información completa, por ejemplo, desde una fuente de datos.
     *
     * @param uuid El UUID en forma de [String] que identifica al [BikeType]. Puede ser null.
     * @return Un objeto [BikeType] correspondiente al UUID dado, o null si el UUID es null.
     */
    @TypeConverter
    fun toBikeType(uuid: String?): BikeType? {
         return uuid?.let { BikeType(it, "name", "type") }
    }
}
