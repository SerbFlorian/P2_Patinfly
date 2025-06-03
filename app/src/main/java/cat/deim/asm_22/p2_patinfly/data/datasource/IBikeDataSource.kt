package cat.deim.asm_22.p2_patinfly.data.datasource

import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.BikeModel

/**
 * Interfaz para la fuente de datos de bicicletas, que define operaciones CRUD.
 */
interface IBikeDataSource {

    /**
     * Inserta un nuevo [BikeModel] en la fuente de datos.
     *
     * @param bikeModel El modelo de bicicleta a insertar.
     * @return `true` si la inserción fue exitosa, `false` en caso contrario.
     */
    fun insert(bikeModel: BikeModel): Boolean

    /**
     * Inserta un nuevo [BikeModel] o actualiza uno existente en la fuente de datos.
     *
     * @param bikeModel El modelo de bicicleta a insertar o actualizar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    fun insertOrUpdate(bikeModel: BikeModel): Boolean

    /**
     * Obtiene una bicicleta cualquiera de la fuente de datos.
     *
     * @return Un [BikeModel] si existe alguna bicicleta almacenada, o `null` si no hay ninguna.
     */
    fun getBike(): BikeModel?

    /**
     * Obtiene una bicicleta por su identificador único.
     *
     * @param uuid Identificador único de la bicicleta.
     * @return El [BikeModel] correspondiente al identificador, o `null` si no se encuentra.
     */
    fun getBikeById(uuid: String): BikeModel?

    /**
     * Actualiza la información de una bicicleta existente.
     *
     * @param bikeModel El modelo de bicicleta con los datos actualizados.
     * @return El [BikeModel] actualizado si la operación fue exitosa, o `null` en caso contrario.
     */
    fun update(bikeModel: BikeModel): BikeModel?

    /**
     * Elimina una bicicleta de la fuente de datos.
     *
     * @return El [BikeModel] eliminado si la operación fue exitosa, o `null` si no se pudo eliminar.
     */
    fun delete(): BikeModel?

    /**
     * Obtiene todas las bicicletas almacenadas en la fuente de datos.
     *
     * @return Una colección con todos los modelos de bicicleta disponibles.
     */
    fun getAll(): Collection<BikeModel>
}
