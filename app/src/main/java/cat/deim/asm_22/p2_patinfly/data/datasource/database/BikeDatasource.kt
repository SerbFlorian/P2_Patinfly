package cat.deim.asm_22.p2_patinfly.data.datasource.database

import androidx.room.*
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO

/**
 * Interfaz DAO (Data Access Object) para gestionar las operaciones de acceso
 * a la base de datos relacionadas con las bicicletas.
 */
@Dao
interface BikeDatasource {

    /**
     * Guarda una bicicleta en la base de datos.
     * Si ya existe una con el mismo UUID, la reemplaza.
     *
     * @param bike objeto BikeDTO que se desea guardar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(bike: BikeDTO)

    /**
     * Recupera una bicicleta utilizando su UUID.
     *
     * @param uuid identificador único de la bicicleta
     * @return la bicicleta correspondiente o null si no se encuentra
     */
    @Query("SELECT * FROM bike WHERE uuid = :uuid")
    suspend fun getByUUID(uuid: String): BikeDTO?

    /**
     * Recupera todas las bicicletas almacenadas en la base de datos.
     *
     * @return lista de todas las bicicletas
     */
    @Query("SELECT * FROM bike")
    suspend fun getAll(): List<BikeDTO>

    /**
     * Elimina una bicicleta de la base de datos.
     *
     * @param bike objeto BikeDTO que se desea eliminar
     */
    @Delete
    suspend fun delete(bike: BikeDTO)

    /**
     * Actualiza los datos de una bicicleta existente.
     *
     * @param bike objeto BikeDTO con los nuevos datos
     */
    @Update
    suspend fun updateBike(bike: BikeDTO)

    /**
     * Actualiza el estado de actividad de una bicicleta.
     *
     * @param bikeId identificador único de la bicicleta
     * @param isActive nuevo estado de actividad (true para activa, false para inactiva)
     */
    @Query("UPDATE bike SET isActive = :isActive WHERE uuid = :bikeId")
    suspend fun updateBikeStatus(bikeId: String, isActive: Boolean)

    /**
     * Actualiza el estado de alquiler de una bicicleta.
     *
     * @param bikeId identificador único de la bicicleta
     * @param isRented nuevo estado de alquiler (true si está alquilada, false si no)
     */
    @Query("UPDATE bike SET isRented = :isRented WHERE uuid = :bikeId")
    suspend fun updateBikeRentStatus(bikeId: String, isRented: Boolean)

    /**
     * Recupera una bicicleta a partir de su UUID.
     *
     * @param bikeId identificador único de la bicicleta
     * @return la bicicleta correspondiente o null si no se encuentra
     */
    @Query("SELECT * FROM bike WHERE uuid = :bikeId")
    suspend fun getBikeById(bikeId: String): BikeDTO?

    /**
     * Recupera una única bicicleta de la base de datos.
     *
     * @return una bicicleta o null si la base de datos está vacía
     */
    @Query("SELECT * FROM bike LIMIT 1")
    suspend fun getBike(): BikeDTO?
}
