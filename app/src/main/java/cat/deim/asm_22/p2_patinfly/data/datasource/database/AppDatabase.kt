package cat.deim.asm_22.p2_patinfly.data.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.SystemPricingPlanDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.UserDTO
import cat.deim.asm_22.p2_patinfly.domain.models.converter.BikeTypeConverter

/**
 * Clase abstracta que representa la base de datos principal de la aplicación.
 * Define las entidades incluidas, la versión de la base de datos y los conversores utilizados.
 */
@Database(
    entities = [UserDTO::class, BikeDTO::class, SystemPricingPlanDTO::class],
    version = 33
)
@TypeConverters(BikeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Devuelve una instancia del DAO para acceder a los datos de usuario.
     * @return instancia de UserDatasource
     */
    abstract fun userDataSource(): UserDatasource

    /**
     * Devuelve una instancia del DAO para acceder a los datos de bicicletas.
     * @return instancia de BikeDatasource
     */
    abstract fun bikeDataSource(): BikeDatasource

    /**
     * Devuelve una instancia del DAO para acceder a los datos del plan de precios del sistema.
     * @return instancia de SystemPricingPlanDataSource
     */
    abstract fun systemPricingPlanDataSource(): SystemPricingPlanDataSource

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene una instancia única de la base de datos. Si no existe, se crea utilizando Room.
         * Utiliza migración destructiva por defecto para manejar cambios de versión.
         * @param context contexto de la aplicación
         * @return instancia única de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "patinfly_25_database"
                ).fallbackToDestructiveMigration()
                    .build()
            }
    }
}
