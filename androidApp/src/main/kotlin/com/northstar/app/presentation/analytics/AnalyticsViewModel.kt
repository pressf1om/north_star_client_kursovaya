package com.northstar.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.AnalyticsVariantDto
import com.northstar.app.data.api.RouteDto
import com.northstar.app.data.remote.apiErrorMessage
import com.northstar.app.domain.analytics.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val variants: List<AnalyticsVariantDto> = emptyList(),
    val routes: List<RouteDto> = emptyList(),
    val numberOfRoad: String = "",
    val dateStart: String = "",
    val dateEnd: String = "",
    val selectedRouteId: Int? = null,
    val autodorCost: String = "",
    val fuelCost: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val routesResult = analyticsRepository.listRoutesForPicker()
            val variantsResult = analyticsRepository.listVariants()
            val routes = routesResult.getOrElse { emptyList() }
            val variants = variantsResult.getOrElse { emptyList() }
            val error = when {
                variantsResult.isFailure -> variantsResult.exceptionOrNull()
                    ?.apiErrorMessage("Не удалось загрузить аналитику")
                routesResult.isFailure -> routesResult.exceptionOrNull()
                    ?.apiErrorMessage("Не удалось загрузить маршруты")
                else -> null
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    routes = routes,
                    variants = variants,
                    errorMessage = error,
                    selectedRouteId = it.selectedRouteId ?: routes.firstOrNull()?.id,
                )
            }
        }
    }

    fun onNumberOfRoadChange(value: String) = _uiState.update { it.copy(numberOfRoad = value) }
    fun onDateStartChange(value: String) = _uiState.update { it.copy(dateStart = value) }
    fun onDateEndChange(value: String) = _uiState.update { it.copy(dateEnd = value) }
    fun onRouteSelected(routeId: Int) = _uiState.update { it.copy(selectedRouteId = routeId) }
    fun onAutodorCostChange(value: String) = _uiState.update { it.copy(autodorCost = value) }
    fun onFuelCostChange(value: String) = _uiState.update { it.copy(fuelCost = value) }

    fun addVariant() {
        val state = _uiState.value
        val number = state.numberOfRoad.trim().toIntOrNull()
        if (number == null || number <= 0) {
            _uiState.update { it.copy(errorMessage = "Укажите номер рейса") }
            return
        }
        if (state.dateStart.trim().isEmpty() || state.dateEnd.trim().isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Укажите даты") }
            return
        }
        val routeId = state.selectedRouteId
        if (routeId == null) {
            _uiState.update { it.copy(errorMessage = "Выберите маршрут") }
            return
        }
        val autodor = state.autodorCost.trim().toIntOrNull()
        val fuel = state.fuelCost.trim().toIntOrNull()
        if (autodor == null || fuel == null || autodor < 0 || fuel < 0) {
            _uiState.update { it.copy(errorMessage = "Укажите стоимости Автодора и топлива") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            analyticsRepository.addVariant(
                numberOfRoad = number,
                dateStart = state.dateStart.trim(),
                dateEnd = state.dateEnd.trim(),
                routeId = routeId,
                cost = autodor,
                fuelCost = fuel,
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        numberOfRoad = "",
                        dateStart = "",
                        dateEnd = "",
                        autodorCost = "",
                        fuelCost = "",
                        successMessage = "Вариант добавлен",
                    )
                }
                load()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.apiErrorMessage("Не удалось добавить вариант"),
                    )
                }
            }
        }
    }

    fun clearVariants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            analyticsRepository.clearVariants()
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Варианты очищены") }
                    load()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.apiErrorMessage("Не удалось очистить"),
                        )
                    }
                }
        }
    }
}
