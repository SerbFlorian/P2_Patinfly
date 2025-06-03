package cat.deim.asm_22.p2_patinfly.presentation.detailBike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository

/**
 * Factory para crear instancias de [DetailBikeViewModel] con dependencias personalizadas.
 *
 * @property bikeRepository Repositorio de bicicletas que será inyectado en el ViewModel.
 */
class DetailBikeViewModelFactory(private val bikeRepository: BikeRepository) : ViewModelProvider.Factory {

    /**
     * Crea una instancia de [ViewModel] del tipo solicitado.
     *
     * @param modelClass Clase del ViewModel que se desea crear.
     * @return Instancia del ViewModel solicitada, en este caso [DetailBikeViewModel].
     * @throws IllegalArgumentException si la clase del ViewModel no es compatible.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailBikeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailBikeViewModel(bikeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
