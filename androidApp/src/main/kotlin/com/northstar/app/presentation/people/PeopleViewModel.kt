package com.northstar.app.presentation.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.UserDto
import com.northstar.app.domain.users.UsersRepository
import com.northstar.app.presentation.session.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PeopleUiState(
    val isLoading: Boolean = true,
    val users: List<UserDto> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            usersRepository.listUsers()
                .onSuccess { users ->
                    _uiState.update { it.copy(isLoading = false, users = users) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось загрузить список",
                        )
                    }
                }
        }
    }
}

fun UserDto.cardAccentPending(): Boolean = status == UserStatus.PENDING

fun UserDto.secondaryLine(): String =
    if (status == UserStatus.DRIVER && carNumber != null) {
        UserStatus.DRIVER
    } else {
        email
    }
