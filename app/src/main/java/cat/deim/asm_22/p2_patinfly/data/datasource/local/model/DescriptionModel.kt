package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.DescriptionDomain

/**
 * Modelo de datos que representa una descripción con su texto y el idioma en el que está escrita.
 * Este modelo se utiliza para mapear los datos de la descripción desde una fuente externa (como JSON)
 * hacia el dominio de la aplicación.
 *
 * @property text El texto de la descripción.
 * @property language El idioma en el que está escrita la descripción.
 */
data class Description(
    val text: String,
    val language: String
) {

    /**
     * Convierte este modelo de datos [Description] a un objeto del dominio [DescriptionDomain].
     *
     * @return Objeto [DescriptionDomain] equivalente, usado en la lógica del dominio de la aplicación.
     */
    fun toDomain(): DescriptionDomain {
        return DescriptionDomain(
            text = text,
            language = language
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [DescriptionDomain] a un modelo de datos [Description].
         *
         * @param domain Objeto del dominio [DescriptionDomain] que se desea transformar.
         * @return Instancia de [Description] correspondiente al modelo utilizado en la fuente de datos.
         */
        fun fromDomain(domain: DescriptionDomain): Description {
            return Description(
                text = domain.text,
                language = domain.language
            )
        }
    }
}
