package com.northstar.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.R
import com.northstar.app.domain.auth.UserAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: UserAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) = _uiState.update { it.copy(username = value, error = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun onPasswordConfirmChange(value: String) = _uiState.update { it.copy(passwordConfirm = value, error = null) }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        when {
            state.username.length < 3 -> _uiState.update { it.copy(error = "Логин не короче 3 символов") }
            state.email.isBlank() || !state.email.contains("@") ->
                _uiState.update { it.copy(error = "Укажите корректную почту") }
            state.password.length < 6 ->
                _uiState.update { it.copy(error = "Пароль не короче 6 символов") }
            state.password != state.passwordConfirm ->
                _uiState.update { it.copy(error = "Пароли не совпадают") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                runCatching {
                    authRepository.signUp(
                        username = state.username.trim(),
                        email = state.email.trim(),
                        password = state.password,
                    )
                }
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = e.toAuthMessage(R.string.auth_error_register),
                            )
                        }
                    }
            }
        }
    }
}
