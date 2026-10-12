package com.northstar.app.presentation.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.CarDto
import com.northstar.app.domain.cars.CarsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FleetUiState(
    val isLoading: Boolean = true,
    val cars: List<CarDto> = emptyList(),
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false,
    val addCarNumber: String = "",
    val addModel: String = "",
    val isSavingCar: Boolean = false,
    val addError: String? = null,
)

@HiltViewModel
class FleetViewModel @Inject constructor(
    private val carsRepository: CarsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            carsRepository.listCars()
                .onSuccess { cars ->
                    _uiState.update { it.copy(isLoading = false, cars = cars) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Не удалось загрузить автопарк")
                    }
                }
        }
    }

    fun openAddDialog() {
        _uiState.update {
            it.copy(showAddDialog = true, addCarNumber = "", addModel = "", addError = null)
        }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, addError = null) }
    }

    fun onAddCarNumberChange(value: String) {
        _uiState.update { it.copy(addCarNumber = value) }
    }

    fun onAddModelChange(value: String) {
        _uiState.update { it.copy(addModel = value) }
    }

    fun submitNewCar() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingCar = true, addError = null) }
            carsRepository.createCar(state.addCarNumber, state.addModel)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSavingCar = false, showAddDialog = false)
                    }
                    load()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isSavingCar = false,
                            addError = "Не удалось добавить машину",
                        )
                    }
                }
        }
    }
}
