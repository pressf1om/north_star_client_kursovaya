package com.northstar.app.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.domain.applications.CompletedApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArchiveUiState(
    val isLoading: Boolean = true,
    val items: List<ApplicationDto> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val completedApplicationsRepository: CompletedApplicationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    fun load(carNumber: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (carNumber.isNullOrBlank()) {
                completedApplicationsRepository.listAll()
            } else {
                completedApplicationsRepository.listByCar(carNumber)
            }
            result
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(isLoading = false, items = list)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось загрузить архив",
                        )
                    }
                }
        }
    }
}
