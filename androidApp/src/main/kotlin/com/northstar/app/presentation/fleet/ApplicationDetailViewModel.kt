package com.northstar.app.presentation.fleet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.AuthApi
import com.northstar.app.domain.applications.ApplicationsRepository
import com.northstar.app.domain.applications.CompletedApplicationsRepository
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
    private val completedApplicationsRepository: CompletedApplicationsRepository,
    private val authApi: AuthApi,
) : ViewModel() {

    private val applicationId: Int = savedStateHandle.get<Int>("applicationId")
        ?: throw IllegalArgumentException("applicationId required")

    private val _uiState = MutableStateFlow(ApplicationDetailUiState())
    val uiState: StateFlow<ApplicationDetailUiState> = _uiState.asStateFlow()

    fun load(fromArchive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val app = if (fromArchive) {
                findCompleted()
            } else {
                applicationsRepository.listApplications().getOrNull()
                    ?.find { it.id == applicationId }
            }
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
    }

    private suspend fun findCompleted(): ApplicationDto? {
        completedApplicationsRepository.listAll().getOrNull()
            ?.find { it.id == applicationId }
            ?.let { return it }
        val carNumber = runCatching { authApi.me().user.carNumber }.getOrNull()
            ?: return null
        return completedApplicationsRepository.listByCar(carNumber).getOrNull()
            ?.find { it.id == applicationId }
    }
}
