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
import com.northstar.app.data.api.CarDto
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.Navy
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun FleetScreen(
    canAddCar: Boolean,
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
            onThemeClick = { /* C17 */ },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            FleetSearchPlaceholder()
            FleetMapPlaceholder()
            Spacer(modifier = Modifier.height(12.dp))
            NorthStarPrimaryButton(
                text = stringResource(R.string.fleet_new_application),
                onClick = { /* C12 */ },
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        color = Ink,
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(uiState.errorMessage!!, color = MutedBrown)
                        TextButton(onClick = viewModel::load) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.cars, key = { it.id }) { car ->
                            FleetCarCard(car = car)
                        }
                    }
                }
            }
        }
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
private fun FleetSearchPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(CreamInput)
            .border(2.dp, Ink)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("⌕", fontWeight = FontWeight.Bold, color = Ink)
        Text(
            text = stringResource(R.string.fleet_search_hint),
            modifier = Modifier.padding(start = 8.dp),
            color = MutedBrown,
            fontSize = 14.sp,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun FleetMapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(Navy),
    ) {
        listOf(
            Triple("СПб", 14.dp, 4.dp),
            Triple("Москва", 42.dp, 22.dp),
            Triple("Казань", 118.dp, 40.dp),
            Triple("Самара", 168.dp, 54.dp),
            Triple("Екб", 220.dp, 30.dp),
            Triple("Нск", 300.dp, 44.dp),
        ).forEach { (name, x, y) ->
            Text(
                text = name,
                color = Amber,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.offset(x = x, y = y),
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun FleetCarCard(car: CarDto) {
    val free = CarStatusLabels.isFree(car.status)
    val accent = if (free) StatusGreen else Amber
    val statusLabel = CarStatusLabels.label(car.status)
    val subtitle = if (free) {
        stringResource(R.string.fleet_no_trip)
    } else {
        stringResource(R.string.fleet_trip_in_progress)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(CreamInput)
            .border(1.dp, CardBorder),
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
                    Text(car.model, fontWeight = FontWeight.ExtraBold, color = Ink, fontSize = 15.sp)
                    Text(subtitle, fontSize = 13.sp, color = MutedBrown)
                }
            }
            Text(
                text = statusLabel.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = accent,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
