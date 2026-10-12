package com.northstar.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.RouteDto
import com.northstar.app.data.remote.apiErrorMessage
import com.northstar.app.domain.routes.RoutesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutesListUiState(
    val isLoading: Boolean = true,
    val routes: List<RouteDto> = emptyList(),
    val errorMessage: String? = null,
)

data class NewRouteUiState(
    val cityStart: String = "",
    val cityEnd: String = "",
    val platonKm: String = "",
    val autodorKm: String = "",
    val ollKm: String = "",
    val citiesOnRoute: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RoutesListViewModel @Inject constructor(
    private val routesRepository: RoutesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesListUiState())
    val uiState: StateFlow<RoutesListUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            routesRepository.listRoutes()
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, routes = list) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.apiErrorMessage("Не удалось загрузить маршруты"),
                        )
                    }
                }
        }
    }
}

@HiltViewModel
class NewRouteViewModel @Inject constructor(
    private val routesRepository: RoutesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewRouteUiState())
    val uiState: StateFlow<NewRouteUiState> = _uiState.asStateFlow()

    fun onCityStartChange(value: String) = _uiState.update { it.copy(cityStart = value) }
    fun onCityEndChange(value: String) = _uiState.update { it.copy(cityEnd = value) }
    fun onPlatonKmChange(value: String) = _uiState.update { it.copy(platonKm = value) }
    fun onAutodorKmChange(value: String) = _uiState.update { it.copy(autodorKm = value) }
    fun onOllKmChange(value: String) = _uiState.update { it.copy(ollKm = value) }
    fun onCitiesChange(value: String) = _uiState.update { it.copy(citiesOnRoute = value) }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        val cityStart = state.cityStart.trim()
        val cityEnd = state.cityEnd.trim()
        if (cityStart.isEmpty() || cityEnd.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Укажите города начала и конца") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            routesRepository.createRoute(
                cityStart = cityStart,
                cityEnd = cityEnd,
                platonKm = state.platonKm.trim().toIntOrNull(),
                autodorKm = state.autodorKm.trim().toIntOrNull(),
                ollKm = state.ollKm.trim().toIntOrNull(),
                citiesOnRoute = state.citiesOnRoute,
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.apiErrorMessage("Не удалось сохранить маршрут"),
                    )
                }
            }
        }
    }
}
