package com.northstar.app.presentation.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.domain.applications.ApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DriverTripUiState(
    val isLoading: Boolean = true,
    val carNumber: String? = null,
    val application: ApplicationDto? = null,
    val errorMessage: String? = null,
    val noTrip: Boolean = false,
)

@HiltViewModel
class DriverTripViewModel @Inject constructor(
    private val applicationsRepository: ApplicationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverTripUiState())
    val uiState: StateFlow<DriverTripUiState> = _uiState.asStateFlow()

    fun load(carNumber: String?) {
        if (carNumber.isNullOrBlank()) {
            _uiState.update {
                it.copy(isLoading = false, carNumber = null, application = null, noTrip = true)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, carNumber = carNumber, errorMessage = null, noTrip = false)
            }
            applicationsRepository.getByCarNumber(carNumber)
                .onSuccess { app ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            application = app,
                            noTrip = app == null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось загрузить рейс",
                        )
                    }
                }
        }
    }
}
