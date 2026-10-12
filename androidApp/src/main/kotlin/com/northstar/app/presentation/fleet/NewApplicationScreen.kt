package com.northstar.app.presentation.fleet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.northStarOutlinedFieldColors

@Composable
fun NewApplicationScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: NewApplicationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.fleet_new_application),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            when {
                uiState.isLoadingCars -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                uiState.noFreeCars -> {
                    Text(
                        text = stringResource(R.string.new_application_no_free_cars),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
                else -> {
                    FormField(
                        label = stringResource(R.string.new_application_coord_start),
                        value = uiState.coordStart,
                        onValueChange = viewModel::onCoordStartChange,
                        placeholder = stringResource(R.string.new_application_coord_hint),
                    )
                    FormField(
                        label = stringResource(R.string.new_application_coord_end),
                        value = uiState.coordEnd,
                        onValueChange = viewModel::onCoordEndChange,
                        placeholder = stringResource(R.string.new_application_coord_hint),
                    )
                    FormField(
                        label = stringResource(R.string.new_application_date),
                        value = uiState.dateOfStart,
                        onValueChange = viewModel::onDateChange,
                        placeholder = "10.10.2026",
                    )
                    FormField(
                        label = stringResource(R.string.new_application_weight),
                        value = uiState.weight,
                        onValueChange = viewModel::onWeightChange,
                        placeholder = "22",
                    )
                    Text(
                        text = stringResource(R.string.new_application_free_car),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                    FreeCarDropdown(
                        cars = uiState.freeCars,
                        selectedCarNumber = uiState.selectedCarNumber,
                        onCarSelected = viewModel::onCarSelected,
                    )
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    NorthStarPrimaryButton(
                        text = stringResource(R.string.new_application_create),
                        onClick = { viewModel.submit(onCreated) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving,
                    )
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Text(
        text = label,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder) },
        colors = northStarOutlinedFieldColors(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FreeCarDropdown(
    cars: List<com.northstar.app.data.api.CarDto>,
    selectedCarNumber: String?,
    onCarSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = cars.find { it.carNumber == selectedCarNumber }
    val label = selected?.let { "${it.carNumber} · ${it.model}" } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = northStarOutlinedFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            cars.forEach { car ->
                DropdownMenuItem(
                    text = { Text("${car.carNumber} · ${car.model}") },
                    onClick = {
                        onCarSelected(car.carNumber)
                        expanded = false
                    },
                )
            }
        }
    }
}
