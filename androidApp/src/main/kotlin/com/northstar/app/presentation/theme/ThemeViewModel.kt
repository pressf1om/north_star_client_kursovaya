package com.northstar.app.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.preferences.ThemePreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferencesStore: ThemePreferencesStore,
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = themePreferencesStore.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggleTheme() {
        viewModelScope.launch {
            themePreferencesStore.setDarkTheme(!isDarkTheme.value)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            themePreferencesStore.setDarkTheme(enabled)
        }
    }
}
