package cat.deim.asm_22.p2_patinfly.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.deim.asm_22.p2_patinfly.domain.repository.IBikeRepository

/**
 * Fábrica para crear instancias del [BikeListViewModel].
 *
 * Esta clase implementa [ViewModelProvider.Factory] para poder inyectar
 * dependencias en el ViewModel, en este caso un repositorio de bicicletas
 * y el contexto de la aplicación.
 *
 * @property repository Repositorio de bicicletas para inyectar en el ViewModel.
 * @property context Contexto de la aplicación necesario para el ViewModel.
 */
class BikeListViewModelFactory(
    private val repository: IBikeRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    /**
     * Crea una nueva instancia del ViewModel especificado.
     *
     * Verifica que la clase solicitada sea [BikeListViewModel] y crea
     * una instancia pasándole el repositorio y el contexto. Esto permite
     * la inyección de dependencias en ViewModels que no tienen constructor
     * vacío.
     *
     * @param modelClass La clase del ViewModel que se desea crear.
     * @return La instancia creada del ViewModel.
     * @throws IllegalArgumentException Si la clase solicitada no es compatible con [BikeListViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BikeListViewModel::class.java)) {
            return BikeListViewModel(
                repository,
                context = context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
