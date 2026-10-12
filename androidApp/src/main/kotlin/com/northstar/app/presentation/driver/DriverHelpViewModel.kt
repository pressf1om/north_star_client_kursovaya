package com.northstar.app.presentation.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.domain.drivermessages.DriverMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DriverHelpUiState(
    val messageText: String = "",
    val isSending: Boolean = false,
    val sentSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class DriverHelpViewModel @Inject constructor(
    private val driverMessagesRepository: DriverMessagesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverHelpUiState())
    val uiState: StateFlow<DriverHelpUiState> = _uiState.asStateFlow()

    fun onMessageChange(text: String) {
        _uiState.update { it.copy(messageText = text, sentSuccess = false, errorMessage = null) }
    }

    fun send(carNumber: String?) {
        val number = carNumber?.trim()
        if (number.isNullOrEmpty()) {
            _uiState.update { it.copy(errorMessage = "Машина не привязана к учётке") }
            return
        }
        val text = _uiState.value.messageText.trim()
        if (text.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Опишите проблему") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, errorMessage = null, sentSuccess = false) }
            driverMessagesRepository.sendMessage(text, number)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            messageText = "",
                            sentSuccess = true,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = "Не удалось отправить сообщение",
                        )
                    }
                }
        }
    }
}
