package cat.deim.asm_22.p2_patinfly.domain.models

/**
 * Representa las credenciales de un usuario para autenticación.
 *
 * @property email Correo electrónico del usuario.
 * @property password Contraseña del usuario.
 */
data class Credentials(
    var email: String = "",
    var password: String = ""
)
