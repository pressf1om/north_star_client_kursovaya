package com.northstar.app.presentation.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.CarDto
import com.northstar.app.data.search.SearchHistoryStore
import com.northstar.app.data.search.SearchScope
import com.northstar.app.domain.applications.ApplicationsRepository
import com.northstar.app.domain.cars.CarsRepository
import com.northstar.app.presentation.search.matchesQuery
import com.northstar.app.presentation.search.SearchPhase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FleetUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val allCars: List<CarDto> = emptyList(),
    val displayCars: List<CarDto> = emptyList(),
    val applications: List<ApplicationDto> = emptyList(),
    val transitRoutes: List<TransitRoutePins> = emptyList(),
    val searchQuery: String = "",
    val searchPhase: SearchPhase = SearchPhase.IDLE,
    val searchHistory: List<String> = emptyList(),
    val showAddDialog: Boolean = false,
    val addCarNumber: String = "",
    val addModel: String = "",
    val isSavingCar: Boolean = false,
    val addError: String? = null,
)

@HiltViewModel
class FleetViewModel @Inject constructor(
    private val carsRepository: CarsRepository,
    private val applicationsRepository: ApplicationsRepository,
    private val searchHistoryStore: SearchHistoryStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val history = searchHistoryStore.history(SearchScope.FLEET)
            _uiState.update { it.copy(searchHistory = history) }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            runCatching {
                coroutineScope {
                    val carsDeferred = async { carsRepository.listCars().getOrThrow() }
                    val appsDeferred = async { applicationsRepository.listApplications().getOrThrow() }
                    carsDeferred.await() to appsDeferred.await()
                }
            }.onSuccess { (cars, applications) ->
                val transit = buildTransitRoutes(applications)
                _uiState.update { state ->
                    val display = if (state.searchPhase == SearchPhase.RESULTS ||
                        state.searchPhase == SearchPhase.EMPTY
                    ) {
                        filterCars(cars, applications, state.searchQuery)
                    } else {
                        cars
                    }
                    state.copy(
                        isLoading = false,
                        allCars = cars,
                        displayCars = display,
                        applications = applications,
                        transitRoutes = transit,
                        loadError = null,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "Не удалось загрузить автопарк",
                        searchPhase = SearchPhase.ERROR,
                    )
                }
            }
        }
    }

    fun applicationForCar(carNumber: String): ApplicationDto? =
        _uiState.value.applications.find { it.carNow == carNumber }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value, searchPhase = SearchPhase.IDLE) }
        if (value.isBlank()) {
            _uiState.update { it.copy(displayCars = it.allCars, searchPhase = SearchPhase.IDLE) }
        }
    }

    fun submitSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(searchPhase = SearchPhase.LOADING) }
            delay(200)
            searchHistoryStore.addQuery(SearchScope.FLEET, query)
            val history = searchHistoryStore.history(SearchScope.FLEET)
            val state = _uiState.value
            val filtered = filterCars(state.allCars, state.applications, query)
            _uiState.update {
                it.copy(
                    searchHistory = history,
                    displayCars = filtered,
                    searchPhase = if (filtered.isEmpty()) SearchPhase.EMPTY else SearchPhase.RESULTS,
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                displayCars = it.allCars,
                searchPhase = SearchPhase.IDLE,
            )
        }
    }

    fun searchFromHistory(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        submitSearch()
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryStore.clear(SearchScope.FLEET)
            _uiState.update { it.copy(searchHistory = emptyList()) }
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
                    _uiState.update { it.copy(isSavingCar = false, showAddDialog = false) }
                    load()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isSavingCar = false, addError = "Не удалось добавить машину")
                    }
                }
        }
    }

    private fun filterCars(
        cars: List<CarDto>,
        applications: List<ApplicationDto>,
        query: String,
    ): List<CarDto> = cars.filter { car ->
        val app = applications.find { it.carNow == car.carNumber }
        val statusLabel = CarStatusLabels.label(car.status)
        matchesQuery(
            query,
            listOf(
                car.carNumber,
                car.model,
                statusLabel,
                app?.id?.toString() ?: "",
                app?.weight ?: "",
            ),
        )
    }

    private fun buildTransitRoutes(applications: List<ApplicationDto>): List<TransitRoutePins> =
        applications
            .filter { it.status == "5" }
            .mapNotNull { app ->
                val start = GeoMap.parseCoordinatePair(app.coordStart) ?: return@mapNotNull null
                val end = GeoMap.parseCoordinatePair(app.coordEnd) ?: return@mapNotNull null
                TransitRoutePins(
                    startLatitude = start.first,
                    startLongitude = start.second,
                    endLatitude = end.first,
                    endLongitude = end.second,
                )
            }
}
