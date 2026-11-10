package com.northstar.app.presentation.dispatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.DriverMessageDto
import com.northstar.app.data.remote.apiErrorMessage
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
    val replyDrafts: Map<Int, String> = emptyMap(),
    val sendingReplyId: Int? = null,
    val errorMessage: String? = null,
    val replyError: String? = null,
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null, replyError = null) }
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

    fun onReplyChange(messageId: Int, value: String) {
        _uiState.update { state ->
            state.copy(replyDrafts = state.replyDrafts + (messageId to value), replyError = null)
        }
    }

    fun sendReply(messageId: Int) {
        val text = _uiState.value.replyDrafts[messageId]?.trim().orEmpty()
        if (text.isEmpty()) {
            _uiState.update { it.copy(replyError = "Введите ответ") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(sendingReplyId = messageId, replyError = null) }
            driverMessagesRepository.reply(messageId, text)
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            sendingReplyId = null,
                            messages = state.messages.map { if (it.id == updated.id) updated else it },
                            replyDrafts = state.replyDrafts - messageId,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            sendingReplyId = null,
                            replyError = error.apiErrorMessage("Не удалось отправить ответ"),
                        )
                    }
                }
        }
    }
}
