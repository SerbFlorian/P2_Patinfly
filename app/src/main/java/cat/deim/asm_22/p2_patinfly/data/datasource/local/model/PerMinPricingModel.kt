package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.PricingMinRateDomain

/**
 * Modelo de datos que representa la tarifa mínima aplicada con un intervalo de aplicación.
 * Este modelo se utiliza para mapear los datos de la tarifa desde una fuente externa (como JSON)
 * hacia el dominio de la aplicación.
 *
 * @property start Valor inicial desde el cual se aplica la tarifa mínima.
 * @property rate Tarifa mínima aplicada.
 * @property interval Intervalo en el que se aplica la tarifa mínima (por ejemplo, cada X unidades de tiempo o distancia).
 */
data class PricingMinRate(
    val start: Double,
    val rate: Double,
    val interval: Int
) {

    /**
     * Convierte el modelo de datos [PricingMinRate] a un objeto del dominio [PricingMinRateDomain].
     *
     * @return Objeto [PricingMinRateDomain] que representa la tarifa mínima en el dominio de la aplicación.
     */
    fun toDomain(): PricingMinRateDomain {
        return PricingMinRateDomain(
            start = start,
            rate = rate,
            interval = interval
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [PricingMinRateDomain] a un modelo de datos [PricingMinRate].
         *
         * @param domain Objeto del dominio [PricingMinRateDomain] que se desea convertir a modelo de datos.
         * @return [PricingMinRate] que representa la tarifa mínima en el formato utilizado por la fuente de datos.
         */
        fun fromDomain(domain: PricingMinRateDomain): PricingMinRate {
            return PricingMinRate(
                start = domain.start,
                rate = domain.rate,
                interval = domain.interval
            )
        }
    }
}
