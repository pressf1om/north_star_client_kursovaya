package com.northstar.app.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.AuthApi
import com.northstar.app.data.auth.TokenStore
import com.northstar.app.domain.auth.UserAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SessionStatus {
    Loading,
    SignedOut,
    SignedIn,
}

data class CurrentUser(
    val id: Int,
    val email: String,
    val username: String,
    val status: String,
    val carNumber: String? = null,
    val carModel: String? = null,
) {
    val isAdmin: Boolean get() = status == UserStatus.ADMIN
    val isDispatcher: Boolean get() = status == UserStatus.DISPATCHER
    val isDriver: Boolean get() = status == UserStatus.DRIVER
    val isPending: Boolean get() = status == UserStatus.PENDING
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val authRepository: UserAuthRepository,
    private val authApi: AuthApi,
) : ViewModel() {

    private val _status = MutableStateFlow(SessionStatus.Loading)
    val status: StateFlow<SessionStatus> = _status.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStore.load()
            if (tokenStore.cachedToken.isNullOrBlank()) {
                _status.value = SessionStatus.SignedOut
            } else {
                restoreSession()
            }
        }
    }

    fun onSignedIn() {
        viewModelScope.launch { restoreSession() }
    }

    private suspend fun restoreSession() {
        runCatching { authApi.me() }
            .onSuccess { me ->
                _currentUser.value = me.user.toCurrentUser()
                _status.value = SessionStatus.SignedIn
            }
            .onFailure {
                tokenStore.clear()
                _currentUser.value = null
                _status.value = SessionStatus.SignedOut
            }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching { authRepository.signOut() }
            _currentUser.value = null
            _status.value = SessionStatus.SignedOut
        }
    }
}

private fun com.northstar.app.data.api.UserDto.toCurrentUser() = CurrentUser(
    id = id,
    email = email,
    username = username,
    status = status,
    carNumber = carNumber,
    carModel = carModel,
)
