package com.northstar.app.presentation.fleet

import androidx.lifecycle.SavedStateHandle
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

data class ApplicationDetailUiState(
    val isLoading: Boolean = true,
    val application: ApplicationDto? = null,
    val title: String = "",
    val errorMessage: String? = null,
)

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val applicationsRepository: ApplicationsRepository,
) : ViewModel() {

    private val applicationId: Int = savedStateHandle.get<Int>("applicationId")
        ?: throw IllegalArgumentException("applicationId required")

    private val _uiState = MutableStateFlow(ApplicationDetailUiState())
    val uiState: StateFlow<ApplicationDetailUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            applicationsRepository.listApplications()
                .onSuccess { list ->
                    val app = list.find { it.id == applicationId }
                    if (app == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Заявка не найдена")
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                application = app,
                                title = "Заявка №${app.id}",
                            )
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Не удалось загрузить заявку")
                    }
                }
        }
    }
}
