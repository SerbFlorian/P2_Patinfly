package cat.deim.asm_22.p2_patinfly.data.datasource.database

import androidx.room.*
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.UserDTO
import java.util.UUID

/**
 * Interfaz DAO (Data Access Object) para gestionar las operaciones
 * de acceso a la base de datos relacionadas con los usuarios.
 */
@Dao
interface UserDatasource {

    /**
     * Guarda un usuario en la base de datos.
     * Si ya existe un usuario con el mismo UUID, lo reemplaza.
     *
     * @param user objeto UserDTO que se desea guardar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: UserDTO)

    /**
     * Recupera un usuario por su UUID.
     *
     * @param uuid identificador único del usuario
     * @return el usuario correspondiente o null si no se encuentra
     */
    @Query("SELECT * FROM user WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: UUID): UserDTO?

    /**
     * Recupera un usuario por su correo electrónico.
     *
     * @param email dirección de correo electrónico del usuario
     * @return el usuario correspondiente o null si no se encuentra
     */
    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getByEmail(email: String): UserDTO?

    /**
     * Recupera todos los usuarios almacenados en la base de datos.
     *
     * @return lista de todos los usuarios
     */
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<UserDTO>

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param user objeto UserDTO que se desea eliminar
     */
    @Delete
    suspend fun delete(user: UserDTO)

    /**
     * Recupera un único usuario de la base de datos.
     *
     * @return un usuario o null si la base de datos está vacía
     */
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserDTO?
}
