package com.northstar.app.presentation.dispatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.DriverMessageDto
import com.northstar.app.domain.drivermessages.DriverMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DispatchMessagesUiState(
    val isLoading: Boolean = true,
    val carFilter: String = "",
    val messages: List<DriverMessageDto> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class DispatchMessagesViewModel @Inject constructor(
    private val driverMessagesRepository: DriverMessagesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DispatchMessagesUiState())
    val uiState: StateFlow<DispatchMessagesUiState> = _uiState.asStateFlow()

    fun load() {
        val filter = _uiState.value.carFilter.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            driverMessagesRepository.listMessages(filter.takeIf { it.isNotEmpty() })
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, messages = list) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось загрузить сообщения",
                        )
                    }
                }
        }
    }

    fun onCarFilterChange(value: String) {
        _uiState.update { it.copy(carFilter = value) }
    }

    fun applyFilter() {
        load()
    }

    fun clearFilter() {
        _uiState.update { it.copy(carFilter = "") }
        load()
    }
}
