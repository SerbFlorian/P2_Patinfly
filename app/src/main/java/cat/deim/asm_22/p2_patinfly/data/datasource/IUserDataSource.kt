package cat.deim.asm_22.p2_patinfly.data.datasource

import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.UserModel

/**
 * Interfaz para la fuente de datos de usuarios,
 * que define las operaciones básicas para insertar, actualizar, consultar y eliminar usuarios.
 */
interface IUserDataSource {

    /**
     * Inserta un nuevo usuario en la fuente de datos.
     *
     * @param userModel El modelo de usuario a insertar.
     * @return `true` si la inserción fue exitosa, `false` en caso contrario.
     */
    fun insert(userModel: UserModel): Boolean

    /**
     * Inserta un nuevo usuario o actualiza uno existente en la fuente de datos.
     *
     * @param userModel El modelo de usuario a insertar o actualizar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    fun insertOrUpdate(userModel: UserModel): Boolean

    /**
     * Obtiene un usuario cualquiera de la fuente de datos.
     *
     * @return Un [UserModel] si existe algún usuario almacenado, o `null` si no hay ninguno.
     */
    fun getUser(): UserModel?

    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param uuid Identificador único del usuario.
     * @return El [UserModel] correspondiente al identificador, o `null` si no se encuentra.
     */
    fun getUserById(uuid: String): UserModel?

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param userModel El modelo de usuario con los datos actualizados.
     * @return El [UserModel] actualizado si la operación fue exitosa, o `null` en caso contrario.
     */
    fun update(userModel: UserModel): UserModel?

    /**
     * Elimina un usuario de la fuente de datos.
     *
     * @return El [UserModel] eliminado si la operación fue exitosa, o `null` si no se pudo eliminar.
     */
    fun delete(): UserModel?

    /**
     * Obtiene una lista con todos los usuarios almacenados.
     *
     * @return Una lista con todos los modelos de usuario disponibles.
     */
    fun getAllUsers(): List<UserModel>

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return El [UserModel] correspondiente al correo, o `null` si no se encuentra.
     */
    fun getUser(email: String): UserModel?
}
