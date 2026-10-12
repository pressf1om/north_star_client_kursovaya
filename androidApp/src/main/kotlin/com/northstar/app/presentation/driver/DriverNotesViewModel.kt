package com.northstar.app.presentation.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.domain.notes.CarNote
import com.northstar.app.domain.notes.CarNotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DriverNotesViewModel @Inject constructor(
    private val carNotesRepository: CarNotesRepository,
) : ViewModel() {

    private val _draft = MutableStateFlow("")
    val draftText: StateFlow<String> = _draft.asStateFlow()

    val notes: StateFlow<List<CarNote>> = carNotesRepository.observeNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDraftChange(text: String) {
        _draft.update { text }
    }

    fun addNote() {
        val text = _draft.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            carNotesRepository.addNote(text)
            _draft.update { "" }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            carNotesRepository.clearAll()
        }
    }
}
