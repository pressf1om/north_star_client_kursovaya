package com.northstar.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.remote.apiErrorMessage
import com.northstar.app.domain.routesettings.RouteSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteSettingsUiState(
    val isLoading: Boolean = true,
    val driverSalary: String = "",
    val platonCost: String = "",
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RouteSettingsViewModel @Inject constructor(
    private val routeSettingsRepository: RouteSettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteSettingsUiState())
    val uiState: StateFlow<RouteSettingsUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, savedSuccess = false) }
            routeSettingsRepository.getSettings()
                .onSuccess { settings ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            driverSalary = settings.driverSalary?.toPlainField() ?: "",
                            platonCost = settings.platonCost?.toPlainField() ?: "",
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.apiErrorMessage("Не удалось загрузить тарифы"),
                        )
                    }
                }
        }
    }

    fun onDriverSalaryChange(value: String) {
        _uiState.update { it.copy(driverSalary = value, savedSuccess = false) }
    }

    fun onPlatonCostChange(value: String) {
        _uiState.update { it.copy(platonCost = value, savedSuccess = false) }
    }

    fun save() {
        val salary = _uiState.value.driverSalary.trim().toDoubleOrNull()
        val platon = _uiState.value.platonCost.trim().toDoubleOrNull()
        if (salary == null || platon == null) {
            _uiState.update { it.copy(errorMessage = "Введите числа для обоих тарифов") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, savedSuccess = false) }
            routeSettingsRepository.updateSettings(salary, platon)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSaving = false, savedSuccess = true)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.apiErrorMessage("Не удалось сохранить тарифы"),
                        )
                    }
                }
        }
    }

    private fun Double.toPlainField(): String =
        if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}
