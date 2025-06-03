package cat.deim.asm_22.p2_patinfly.presentation.detailRent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository

/**
 * Factory para crear instancias de [DetailRentBikeViewModel] con un [BikeRepository] proporcionado.
 *
 * @property bikeRepository Repositorio utilizado para obtener datos de bicicletas.
 */
class DetailRentBikeViewModelFactory(private val bikeRepository: BikeRepository) : ViewModelProvider.Factory {

    /**
     * Crea una instancia del ViewModel solicitado, inyectando el repositorio necesario.
     *
     * @param modelClass La clase del ViewModel que se desea crear.
     * @return Una instancia del ViewModel solicitada.
     * @throws IllegalArgumentException si la clase del ViewModel no es compatible.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailRentBikeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailRentBikeViewModel(bikeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
