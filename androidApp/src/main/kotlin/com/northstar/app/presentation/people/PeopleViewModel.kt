package com.northstar.app.presentation.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northstar.app.data.api.UserDto
import com.northstar.app.data.search.SearchHistoryStore
import com.northstar.app.data.search.SearchScope
import com.northstar.app.domain.users.UsersRepository
import com.northstar.app.presentation.search.matchesQuery
import com.northstar.app.presentation.search.SearchPhase
import com.northstar.app.presentation.session.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PeopleUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val allUsers: List<UserDto> = emptyList(),
    val displayUsers: List<UserDto> = emptyList(),
    val searchQuery: String = "",
    val searchPhase: SearchPhase = SearchPhase.IDLE,
    val searchHistory: List<String> = emptyList(),
)

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val searchHistoryStore: SearchHistoryStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val history = searchHistoryStore.history(SearchScope.PEOPLE)
            _uiState.update { it.copy(searchHistory = history) }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            usersRepository.listUsers()
                .onSuccess { users ->
                    _uiState.update { state ->
                        val display = if (state.searchPhase == SearchPhase.RESULTS ||
                            state.searchPhase == SearchPhase.EMPTY
                        ) {
                            filterUsers(users, state.searchQuery)
                        } else {
                            users
                        }
                        state.copy(
                            isLoading = false,
                            allUsers = users,
                            displayUsers = display,
                            loadError = null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "Не удалось загрузить список",
                            searchPhase = SearchPhase.ERROR,
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value, searchPhase = SearchPhase.IDLE) }
        if (value.isBlank()) {
            _uiState.update { it.copy(displayUsers = it.allUsers, searchPhase = SearchPhase.IDLE) }
        }
    }

    fun submitSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(searchPhase = SearchPhase.LOADING) }
            delay(200)
            searchHistoryStore.addQuery(SearchScope.PEOPLE, query)
            val history = searchHistoryStore.history(SearchScope.PEOPLE)
            val filtered = filterUsers(_uiState.value.allUsers, query)
            _uiState.update {
                it.copy(
                    searchHistory = history,
                    displayUsers = filtered,
                    searchPhase = if (filtered.isEmpty()) SearchPhase.EMPTY else SearchPhase.RESULTS,
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                displayUsers = it.allUsers,
                searchPhase = SearchPhase.IDLE,
            )
        }
    }

    fun searchFromHistory(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        submitSearch()
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryStore.clear(SearchScope.PEOPLE)
            _uiState.update { it.copy(searchHistory = emptyList()) }
        }
    }

    private fun filterUsers(users: List<UserDto>, query: String): List<UserDto> =
        users.filter { user ->
            matchesQuery(
                query,
                listOf(
                    user.username,
                    user.email,
                    user.status,
                    user.carNumber ?: "",
                ),
            )
        }
}

fun UserDto.cardAccentPending(): Boolean = status == UserStatus.PENDING

fun UserDto.secondaryLine(): String =
    if (status == UserStatus.DRIVER && carNumber != null) {
        UserStatus.DRIVER
    } else {
        email
    }
