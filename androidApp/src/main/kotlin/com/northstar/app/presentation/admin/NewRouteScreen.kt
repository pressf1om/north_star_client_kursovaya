package com.northstar.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun NewRouteScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: NewRouteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.admin_routes_add_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            RouteFormField(
                label = stringResource(R.string.admin_route_city_start),
                value = uiState.cityStart,
                onValueChange = viewModel::onCityStartChange,
            )
            RouteFormField(
                label = stringResource(R.string.admin_route_city_end),
                value = uiState.cityEnd,
                onValueChange = viewModel::onCityEndChange,
            )
            RouteFormField(
                label = stringResource(R.string.admin_route_platon_km),
                value = uiState.platonKm,
                onValueChange = viewModel::onPlatonKmChange,
            )
            RouteFormField(
                label = stringResource(R.string.admin_route_autodor_km),
                value = uiState.autodorKm,
                onValueChange = viewModel::onAutodorKmChange,
            )
            RouteFormField(
                label = stringResource(R.string.admin_route_oll_km),
                value = uiState.ollKm,
                onValueChange = viewModel::onOllKmChange,
            )
            RouteFormField(
                label = stringResource(R.string.admin_route_cities),
                value = uiState.citiesOnRoute,
                onValueChange = viewModel::onCitiesChange,
                singleLine = false,
            )
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            NorthStarPrimaryButton(
                text = stringResource(R.string.action_save),
                onClick = { viewModel.submit(onCreated) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RouteFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
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
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        colors = northStarOutlinedFieldColors(),
    )
}
