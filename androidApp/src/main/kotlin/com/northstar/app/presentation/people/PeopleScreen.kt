package com.northstar.app.presentation.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.data.api.UserDto
import com.northstar.app.presentation.session.UserStatus
import com.northstar.app.presentation.components.NorthStarInlineSearchBar
import com.northstar.app.presentation.components.NorthStarSearchHistoryPanel
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.presentation.search.SearchPhase
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.northStarSurfaceCard
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun PeopleScreen(
    onAssignRole: (Int) -> Unit,
    onThemeClick: () -> Unit,
    viewModel: PeopleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.tab_people),
            onThemeClick = onThemeClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            var savedQuery by rememberSaveable { mutableStateOf("") }
            if (savedQuery != uiState.searchQuery && uiState.searchQuery.isNotEmpty()) {
                savedQuery = uiState.searchQuery
            }
            NorthStarInlineSearchBar(
                query = savedQuery,
                onQueryChange = { value ->
                    savedQuery = value
                    viewModel.onSearchQueryChange(value)
                },
                onSubmit = viewModel::submitSearch,
                onClear = {
                    savedQuery = ""
                    viewModel.clearSearch()
                },
                placeholder = stringResource(R.string.people_search_hint),
            )
            if (uiState.searchPhase == SearchPhase.IDLE && savedQuery.isBlank()) {
                NorthStarSearchHistoryPanel(
                    history = uiState.searchHistory,
                    onPick = { picked ->
                        savedQuery = picked
                        viewModel.searchFromHistory(picked)
                    },
                    onClearHistory = viewModel::clearHistory,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
            }
            when {
                uiState.searchPhase == SearchPhase.LOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        color = Ink,
                    )
                }
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Ink)
                    }
                }
                uiState.loadError != null || uiState.searchPhase == SearchPhase.ERROR -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = uiState.loadError ?: stringResource(R.string.search_error),
                            color = MutedBrown,
                        )
                        TextButton(onClick = viewModel::load) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
                uiState.searchPhase == SearchPhase.EMPTY -> {
                    Text(
                        text = stringResource(R.string.search_nothing_found),
                        color = MutedBrown,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.displayUsers, key = { it.id }) { user ->
                            PeopleUserCard(
                                user = user,
                                onClick = if (user.status == UserStatus.PENDING) {
                                    { onAssignRole(user.id) }
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleUserCard(
    user: UserDto,
    onClick: (() -> Unit)?,
) {
    val pending = user.cardAccentPending()
    val accent = if (pending) Amber else StatusGreen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .northStarSurfaceCard(),
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(accent),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.username,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = user.secondaryLine(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (!user.carNumber.isNullOrBlank()) {
                    LicensePlate(number = user.carNumber!!)
                }
            }
            Text(
                text = user.status.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = accent,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
