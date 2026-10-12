package com.northstar.app.presentation.fleet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.CarDto
import com.northstar.app.presentation.components.NorthStarInlineSearchBar
import com.northstar.app.presentation.components.NorthStarSearchHistoryPanel
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.presentation.search.SearchPhase
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.Navy
import com.northstar.app.ui.theme.StatusGreen
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun FleetScreen(
    canAddCar: Boolean,
    onNewApplication: () -> Unit,
    onThemeClick: () -> Unit,
    viewModel: FleetViewModel = hiltViewModel(),
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
            title = stringResource(R.string.tab_fleet),
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
                placeholder = stringResource(R.string.fleet_search_hint),
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
            FleetNorthStarMap(
                transitRoutes = uiState.transitRoutes,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            NorthStarPrimaryButton(
                text = stringResource(R.string.fleet_new_application),
                onClick = onNewApplication,
                modifier = Modifier.fillMaxWidth(),
            )
            if (canAddCar) {
                TextButton(
                    onClick = viewModel::openAddDialog,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(stringResource(R.string.fleet_add_car), color = Navy, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        color = Ink,
                    )
                }
                uiState.loadError != null || uiState.searchPhase == SearchPhase.ERROR -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.displayCars, key = { it.id }) { car ->
                            val application = viewModel.applicationForCar(car.carNumber)
                            FleetCarCard(
                                car = car,
                                application = application,
                                canDeleteApplication = canAddCar && application != null,
                                onDeleteApplication = application?.let { app ->
                                    { viewModel.requestDeleteApplication(app.id) }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.deleteApplicationId?.let { applicationId ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteApplication,
            title = { Text(stringResource(R.string.fleet_delete_application_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.fleet_delete_application_message, applicationId))
                    uiState.deleteError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmDeleteApplication,
                    enabled = !uiState.isDeletingApplication,
                ) {
                    Text(stringResource(R.string.fleet_delete_application_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteApplication) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissAddDialog,
            title = { Text(stringResource(R.string.fleet_add_car_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.addCarNumber,
                        onValueChange = viewModel::onAddCarNumberChange,
                        label = { Text(stringResource(R.string.fleet_field_number)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Navy,
                            unfocusedBorderColor = Navy,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.addModel,
                        onValueChange = viewModel::onAddModelChange,
                        label = { Text(stringResource(R.string.fleet_field_model)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Navy,
                            unfocusedBorderColor = Navy,
                        ),
                    )
                    if (uiState.addError != null) {
                        Text(
                            text = uiState.addError!!,
                            color = MutedBrown,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::submitNewCar,
                    enabled = !uiState.isSavingCar,
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissAddDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun FleetCarCard(
    car: CarDto,
    application: ApplicationDto?,
    canDeleteApplication: Boolean,
    onDeleteApplication: (() -> Unit)?,
) {
    val free = CarStatusLabels.isFree(car.status)
    val accent = if (free) StatusGreen else Amber
    val statusLabel = CarStatusLabels.label(car.status)
    val subtitle = when {
        application != null -> {
            val weight = application.weight?.let { "$it т" } ?: ""
            stringResource(R.string.fleet_application_line, application.id, weight)
        }
        free -> stringResource(R.string.fleet_no_trip)
        else -> stringResource(R.string.fleet_trip_in_progress)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
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
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LicensePlate(number = car.carNumber)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        car.model,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                    )
                    Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = statusLabel.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = accent,
                )
                if (canDeleteApplication && onDeleteApplication != null) {
                    TextButton(onClick = onDeleteApplication) {
                        Text(
                            text = stringResource(R.string.fleet_delete_application_action),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
