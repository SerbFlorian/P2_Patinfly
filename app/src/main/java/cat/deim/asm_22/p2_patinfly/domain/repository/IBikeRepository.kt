package cat.deim.asm_22.p2_patinfly.domain.repository

import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.ServerStatus

/**
 * Interfaz que define las operaciones de repositorio para manejar datos de bicicletas.
 */
interface IBikeRepository {

    /**
     * Guarda una bicicleta en el repositorio.
     *
     * @param bike Objeto [Bike] a guardar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    suspend fun setBike(bike: Bike): Boolean

    /**
     * Obtiene una bicicleta del repositorio (sin especificar UUID).
     *
     * @return La bicicleta si existe, o `null` si no se encontró.
     */
    suspend fun getBike(): Bike?

    /**
     * Obtiene una bicicleta por su identificador único.
     *
     * @param uuid Identificador único de la bicicleta.
     * @return La bicicleta correspondiente al UUID, o `null` si no existe.
     */
    suspend fun getBike(uuid: String): Bike?

    /**
     * Actualiza la información de una bicicleta existente.
     *
     * @param bike Objeto [Bike] con los datos actualizados.
     * @return La bicicleta actualizada, o `null` si la actualización falló.
     */
    suspend fun updateBike(bike: Bike): Bike?

    /**
     * Elimina una bicicleta del repositorio.
     *
     * @return La bicicleta eliminada, o `null` si no se pudo eliminar.
     */
    suspend fun deleteBike(): Bike?

    /**
     * Obtiene todas las bicicletas almacenadas en el repositorio.
     *
     * @return Una colección con todas las bicicletas.
     */
    suspend fun getAll(): Collection<Bike>

    /**
     * Obtiene el estado actual del servidor.
     *
     * @return Un objeto [ServerStatus] con la información del servidor.
     */
    suspend fun status(): ServerStatus

    /**
     * Actualiza el estado de alquiler de una bicicleta.
     *
     * @param bike Bicicleta cuyo estado de alquiler será actualizado.
     * @return `true` si la actualización fue exitosa, `false` en caso contrario.
     */
    suspend fun updateBikeRentStatus(bike: Bike): Boolean

    /**
     * Obtiene una lista de bicicletas activas representadas como [BikeDTO].
     *
     * @return Lista de bicicletas activas.
     */
    suspend fun getActiveBikes(): List<BikeDTO>

    /**
     * Obtiene bicicletas filtradas por categoría.
     *
     * @param category Categoría para filtrar las bicicletas.
     * @return Lista de bicicletas que pertenecen a la categoría dada.
     */
    suspend fun getBikesByCategory(category: String): List<Bike>
}
