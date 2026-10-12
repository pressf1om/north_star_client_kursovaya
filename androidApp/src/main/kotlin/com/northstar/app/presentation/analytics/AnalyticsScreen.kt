package com.northstar.app.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.northstar.app.data.api.AnalyticsVariantDto
import com.northstar.app.data.api.RouteDto
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.StatusGreen
import com.northstar.app.ui.theme.northStarOutlinedFieldColors
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
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
        viewModel.load()
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.analytics_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.analytics_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            uiState.successMessage?.let { msg ->
                Text(text = msg, color = StatusGreen, modifier = Modifier.padding(bottom = 8.dp))
            }
            uiState.errorMessage?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                TextButton(onClick = { viewModel.load() }) {
                    Text(stringResource(R.string.action_retry))
                }
            }
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Ink,
                    )
                }
                else -> {
                    AnalyticsForm(
                        uiState = uiState,
                        onNumberChange = viewModel::onNumberOfRoadChange,
                        onDateStartChange = viewModel::onDateStartChange,
                        onDateEndChange = viewModel::onDateEndChange,
                        onRouteSelected = viewModel::onRouteSelected,
                        onAutodorChange = viewModel::onAutodorCostChange,
                        onFuelChange = viewModel::onFuelCostChange,
                        onAdd = { viewModel.addVariant() },
                        isSaving = uiState.isSaving,
                    )
                    if (uiState.variants.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearVariants() },
                            enabled = !uiState.isSaving,
                            modifier = Modifier.padding(top = 4.dp),
                        ) {
                            Text(stringResource(R.string.analytics_clear))
                        }
                    }
                    uiState.variants.forEach { variant ->
                        AnalyticsVariantCard(variant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsForm(
    uiState: AnalyticsUiState,
    onNumberChange: (String) -> Unit,
    onDateStartChange: (String) -> Unit,
    onDateEndChange: (String) -> Unit,
    onRouteSelected: (Int) -> Unit,
    onAutodorChange: (String) -> Unit,
    onFuelChange: (String) -> Unit,
    onAdd: () -> Unit,
    isSaving: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .northStarSurfaceCard()
            .padding(14.dp),
    ) {
        AnalyticsField(
            label = stringResource(R.string.analytics_number_road),
            value = uiState.numberOfRoad,
            onValueChange = onNumberChange,
        )
        AnalyticsField(
            label = stringResource(R.string.analytics_date_start),
            value = uiState.dateStart,
            onValueChange = onDateStartChange,
        )
        AnalyticsField(
            label = stringResource(R.string.analytics_date_end),
            value = uiState.dateEnd,
            onValueChange = onDateEndChange,
        )
        RoutePicker(
            routes = uiState.routes,
            selectedRouteId = uiState.selectedRouteId,
            onRouteSelected = onRouteSelected,
        )
        AnalyticsField(
            label = stringResource(R.string.analytics_autodor_cost),
            value = uiState.autodorCost,
            onValueChange = onAutodorChange,
        )
        AnalyticsField(
            label = stringResource(R.string.analytics_fuel_cost),
            value = uiState.fuelCost,
            onValueChange = onFuelChange,
        )
        NorthStarPrimaryButton(
            text = stringResource(R.string.analytics_add_variant),
            onClick = onAdd,
            enabled = !isSaving && uiState.routes.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        if (uiState.routes.isEmpty()) {
            Text(
                text = stringResource(R.string.analytics_no_routes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun AnalyticsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = northStarOutlinedFieldColors(),
        singleLine = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutePicker(
    routes: List<RouteDto>,
    selectedRouteId: Int?,
    onRouteSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = routes.find { it.id == selectedRouteId }
    val label = selected?.let {
        stringResource(R.string.admin_route_line, it.cityStart, it.cityEnd)
    } ?: stringResource(R.string.analytics_route_pick)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.analytics_route)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = northStarOutlinedFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            routes.forEach { route ->
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.admin_route_line, route.cityStart, route.cityEnd))
                    },
                    onClick = {
                        onRouteSelected(route.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AnalyticsVariantCard(variant: AnalyticsVariantDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .northStarSurfaceCard()
            .padding(14.dp),
    ) {
        Text(
            text = stringResource(R.string.analytics_variant_title, variant.id),
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        variant.numberOfRoad?.let {
            Text(
                text = stringResource(R.string.analytics_number_road_line, it),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (!variant.dateStart.isNullOrBlank() || !variant.dateEnd.isNullOrBlank()) {
            Text(
                text = stringResource(
                    R.string.analytics_dates_line,
                    variant.dateStart ?: "—",
                    variant.dateEnd ?: "—",
                ),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        variant.routeLabel?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        variant.finalCost?.let { cost ->
            Text(
                text = stringResource(R.string.analytics_final_cost, cost),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
