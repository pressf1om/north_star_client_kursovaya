package com.northstar.app.presentation.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.CarDto
import com.northstar.app.data.remote.apiErrorMessage
import com.northstar.app.data.remote.isValidCoordinatePair
import com.northstar.app.domain.applications.ApplicationsRepository
import com.northstar.app.domain.cars.CarsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewApplicationUiState(
    val isLoadingCars: Boolean = true,
    val freeCars: List<CarDto> = emptyList(),
    val noFreeCars: Boolean = false,
    val coordStart: String = "",
    val coordEnd: String = "",
    val dateOfStart: String = defaultDate(),
    val weight: String = "",
    val selectedCarNumber: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

private fun defaultDate(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

@HiltViewModel
class NewApplicationViewModel @Inject constructor(
    private val carsRepository: CarsRepository,
    private val applicationsRepository: ApplicationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewApplicationUiState())
    val uiState: StateFlow<NewApplicationUiState> = _uiState.asStateFlow()

    init {
        loadFreeCars()
    }

    fun loadFreeCars() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCars = true, errorMessage = null) }
            carsRepository.listCars(status = "1")
                .onSuccess { cars ->
                    _uiState.update {
                        it.copy(
                            isLoadingCars = false,
                            freeCars = cars,
                            noFreeCars = cars.isEmpty(),
                            selectedCarNumber = cars.firstOrNull()?.carNumber,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingCars = false,
                            errorMessage = "Не удалось загрузить свободные машины",
                        )
                    }
                }
        }
    }

    fun onCoordStartChange(value: String) {
        _uiState.update { it.copy(coordStart = value) }
    }

    fun onCoordEndChange(value: String) {
        _uiState.update { it.copy(coordEnd = value) }
    }

    fun onDateChange(value: String) {
        _uiState.update { it.copy(dateOfStart = value) }
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weight = value) }
    }

    fun onCarSelected(carNumber: String) {
        _uiState.update { it.copy(selectedCarNumber = carNumber) }
    }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        val car = state.selectedCarNumber
        if (state.noFreeCars || car == null) {
            _uiState.update { it.copy(errorMessage = "Нет свободных машин") }
            return
        }
        val coordStart = state.coordStart.trim()
        val coordEnd = state.coordEnd.trim()
        val weight = state.weight.trim()
        val date = state.dateOfStart.trim()
        when {
            !isValidCoordinatePair(coordStart) -> {
                _uiState.update {
                    it.copy(errorMessage = "Координаты начала: широта,долгота (например 55.75,37.61)")
                }
                return
            }
            !isValidCoordinatePair(coordEnd) -> {
                _uiState.update {
                    it.copy(errorMessage = "Координаты конца: широта,долгота (например 55.75,37.61)")
                }
                return
            }
            date.isEmpty() -> {
                _uiState.update { it.copy(errorMessage = "Укажите дату выезда") }
                return
            }
            weight.isEmpty() -> {
                _uiState.update { it.copy(errorMessage = "Укажите вес груза") }
                return
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            applicationsRepository.createApplication(
                coordStart = coordStart,
                coordEnd = coordEnd,
                dateOfStart = date,
                weight = weight,
                carNow = car,
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.apiErrorMessage("Не удалось создать заявку"),
                    )
                }
            }
        }
    }
}
