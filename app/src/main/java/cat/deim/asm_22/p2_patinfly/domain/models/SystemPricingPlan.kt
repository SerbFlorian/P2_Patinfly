package cat.deim.asm_22.p2_patinfly.domain.models

/**
 * Representa el plan de precios del sistema con metadatos y datos detallados.
 *
 * @property lastUpdated Fecha de la última actualización del plan de precios.
 * @property ttl Tiempo en segundos que indica la validez del plan de precios (time to live).
 * @property version Versión del plan de precios.
 * @property data Datos específicos del plan de precios.
 */
data class SystemPricingPlan(
    val lastUpdated: String,
    val ttl: Int,
    val version: String,
    val data: DataPlanDomain
)

/**
 * Contiene la lista de planes disponibles en el sistema.
 *
 * @property plans Lista de planes de precios.
 */
data class DataPlanDomain(
    val plans: List<PlanDomain>
)

/**
 * Representa un plan de precios individual.
 *
 * @property planId Identificador único del plan.
 * @property name Lista de nombres del plan en diferentes idiomas.
 * @property currency Moneda en la que se expresa el precio.
 * @property price Precio base del plan.
 * @property isTaxable Indica si el plan está sujeto a impuestos.
 * @property description Lista de descripciones del plan en diferentes idiomas.
 * @property perKmPricing Lista de tarifas aplicadas por kilómetro.
 * @property perMinPricing Lista de tarifas aplicadas por minuto.
 */
data class PlanDomain(
    val planId: String,
    val name: List<NameDomain>,
    val currency: String,
    val price: Double,
    val isTaxable: Boolean,
    val description: List<DescriptionDomain>,
    val perKmPricing: List<PricingKmRateDomain>,
    val perMinPricing: List<PricingMinRateDomain>
)

/**
 * Representa un nombre traducido para un plan o elemento.
 *
 * @property text Texto del nombre.
 * @property language Código del idioma del texto (por ejemplo, "en", "es").
 */
data class NameDomain(
    val text: String,
    val language: String
)

/**
 * Representa una descripción traducida para un plan o elemento.
 *
 * @property text Texto de la descripción.
 * @property language Código del idioma del texto.
 */
data class DescriptionDomain(
    val text: String,
    val language: String
)

/**
 * Representa la tarifa aplicada por kilómetro.
 *
 * @property start Valor a partir del cual se aplica esta tarifa.
 * @property rate Precio por unidad.
 * @property interval Intervalo en unidades para aplicar la tarifa.
 */
data class PricingKmRateDomain(
    val start: Double,
    val rate: Double,
    val interval: Int
)

/**
 * Representa la tarifa aplicada por minuto.
 *
 * @property start Valor a partir del cual se aplica esta tarifa.
 * @property rate Precio por unidad.
 * @property interval Intervalo en unidades para aplicar la tarifa.
 */
data class PricingMinRateDomain(
    val start: Double,
    val rate: Double,
    val interval: Int
)
