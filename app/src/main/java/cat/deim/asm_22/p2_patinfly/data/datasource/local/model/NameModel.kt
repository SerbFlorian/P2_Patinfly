package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.NameDomain

/**
 * Modelo de datos que representa un nombre con su texto y el idioma en el que está escrito.
 * Este modelo se utiliza para mapear los datos del nombre desde una fuente externa (como JSON)
 * hacia el dominio de la aplicación.
 *
 * @property text El texto del nombre.
 * @property language El idioma en el que está escrito el nombre.
 */
data class Name(
    val text: String,
    val language: String
) {

    /**
     * Convierte este modelo de datos [Name] a un objeto del dominio [NameDomain].
     *
     * @return Objeto [NameDomain] equivalente, utilizado en la lógica del dominio de la aplicación.
     */
    fun toDomain(): NameDomain {
        return NameDomain(
            text = text,
            language = language
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [NameDomain] a un modelo de datos [Name].
         *
         * @param domain Objeto del dominio [NameDomain] que se desea transformar.
         * @return Instancia de [Name] correspondiente al modelo utilizado por la fuente de datos.
         */
        fun fromDomain(domain: NameDomain): Name {
            return Name(
                text = domain.text,
                language = domain.language
            )
        }
    }
}
