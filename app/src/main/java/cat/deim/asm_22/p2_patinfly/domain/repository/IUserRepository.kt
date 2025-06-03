package cat.deim.asm_22.p2_patinfly.domain.repository

import cat.deim.asm_22.p2_patinfly.domain.models.User

/**
 * Interfaz que define las operaciones para manejar usuarios en el repositorio.
 */
interface IUserRepository {

    /**
     * Guarda un usuario en el repositorio.
     *
     * @param user Objeto [User] a guardar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    suspend fun setUser(user: User): Boolean

    /**
     * Obtiene un usuario almacenado.
     *
     * @return El usuario si existe, o `null` si no se encontró ninguno.
     */
    suspend fun getUser(): User?

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return El usuario correspondiente al correo si existe, o `null` si no se encontró.
     */
    suspend fun getUser(email: String): User?

    /**
     * Actualiza un usuario existente.
     *
     * @param user Objeto [User] con los datos actualizados.
     * @return El usuario actualizado, o `null` si la actualización falló.
     */
    suspend fun updateUser(user: User): User?

    /**
     * Elimina un usuario almacenado.
     *
     * @return El usuario eliminado, o `null` si no se pudo eliminar ninguno.
     */
    suspend fun deleteUser(): User?

    /**
     * Obtiene todos los usuarios almacenados.
     *
     * @return Una colección con todos los usuarios (tipo genérico `Any` puede ser ajustado según implementación).
     */
    suspend fun getAllUsers(): Any
}
