package com.northstar.app.presentation.people

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.CarDto
import com.northstar.app.data.api.UserDto
import com.northstar.app.domain.cars.CarsRepository
import com.northstar.app.domain.users.UsersRepository
import com.northstar.app.presentation.session.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AssignableRole(val apiStatus: String) {
    DISPATCHER(UserStatus.DISPATCHER),
    DRIVER(UserStatus.DRIVER),
}

data class AssignRoleUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val user: UserDto? = null,
    val cars: List<CarDto> = emptyList(),
    val selectedRole: AssignableRole = AssignableRole.DRIVER,
    val selectedCarId: Int? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class AssignRoleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val usersRepository: UsersRepository,
    private val carsRepository: CarsRepository,
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>("userId")
        ?: throw IllegalArgumentException("userId required")

    private val _uiState = MutableStateFlow(AssignRoleUiState())
    val uiState: StateFlow<AssignRoleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val usersResult = usersRepository.listUsers()
            val carsResult = carsRepository.listCars()
            usersResult.onSuccess { users ->
                val user = users.find { it.id == userId }
                if (user == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Пользователь не найден")
                    }
                    return@launch
                }
                carsResult.onSuccess { cars ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            cars = cars,
                            selectedCarId = cars.firstOrNull()?.id,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, user = user, errorMessage = "Не удалось загрузить парк")
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Не удалось загрузить пользователя")
                }
            }
        }
    }

    fun onRoleSelected(role: AssignableRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onCarSelected(carId: Int) {
        _uiState.update { it.copy(selectedCarId = carId) }
    }

    fun confirm(onSuccess: () -> Unit) {
        val state = _uiState.value
        val user = state.user ?: return
        if (state.selectedRole == AssignableRole.DRIVER && state.selectedCarId == null) {
            _uiState.update { it.copy(errorMessage = "Выберите машину") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            usersRepository.updateUserRole(
                userId = user.id,
                status = state.selectedRole.apiStatus,
                carId = if (state.selectedRole == AssignableRole.DRIVER) state.selectedCarId else null,
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Не удалось сохранить роль")
                }
            }
        }
    }
}
