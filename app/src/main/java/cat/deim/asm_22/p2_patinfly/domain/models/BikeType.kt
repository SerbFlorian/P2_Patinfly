package cat.deim.asm_22.p2_patinfly.domain.models

/**
 * Representa el tipo de bicicleta.
 *
 * @property uuid Identificador único del tipo de bicicleta.
 * @property name Nombre descriptivo del tipo de bicicleta.
 * @property type Categoría o clasificación del tipo de bicicleta.
 */
data class BikeType(
    val uuid: String,
    val name: String,
    val type: String
)
