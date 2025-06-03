package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.PricingKmRateDomain

/**
 * Modelo de datos que representa la tarifa por kilómetro con un intervalo de aplicación.
 * Este modelo se utiliza para mapear los datos de la tarifa desde una fuente externa (como JSON)
 * hacia el dominio de la aplicación.
 *
 * @property start Valor inicial del rango de kilómetros desde el cual se aplica la tarifa.
 * @property rate Tarifa monetaria que se aplica por kilómetro dentro del rango especificado.
 * @property interval Intervalo de kilómetros al que se aplica la tarifa (por ejemplo, cada 10 km).
 */
data class PricingKmRate(
    val start: Double,
    val rate: Double,
    val interval: Int
) {

    /**
     * Convierte este modelo de datos [PricingKmRate] a un objeto del dominio [PricingKmRateDomain].
     *
     * @return Objeto [PricingKmRateDomain] que representa esta tarifa en la lógica del dominio de la aplicación.
     */
    fun toDomain(): PricingKmRateDomain {
        return PricingKmRateDomain(
            start = start,
            rate = rate,
            interval = interval
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [PricingKmRateDomain] a un modelo de datos [PricingKmRate].
         *
         * @param domain Objeto del dominio [PricingKmRateDomain] que se desea transformar.
         * @return Instancia de [PricingKmRate] en el formato utilizado por la fuente de datos local.
         */
        fun fromDomain(domain: PricingKmRateDomain): PricingKmRate {
            return PricingKmRate(
                start = domain.start,
                rate = domain.rate,
                interval = domain.interval
            )
        }
    }
}
