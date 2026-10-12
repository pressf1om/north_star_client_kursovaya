package com.northstar.app.presentation.fleet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
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
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.presentation.driver.DriverTripStatusUi
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun ApplicationDetailScreen(
    onBack: () -> Unit,
    viewModel: ApplicationDetailViewModel = hiltViewModel(),
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
            title = uiState.title,
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = Ink)
                }
                uiState.errorMessage != null -> {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { viewModel.load() }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
                uiState.application != null -> {
                    val app = uiState.application!!
                    DetailRow(
                        label = stringResource(R.string.application_detail_car),
                        value = app.carNow ?: "—",
                    )
                    DetailRow(
                        label = stringResource(R.string.application_detail_status),
                        value = DriverTripStatusUi.statusLabel(app.status),
                    )
                    DetailRow(
                        label = stringResource(R.string.application_detail_weight),
                        value = app.weight?.let { "$it т" } ?: "—",
                    )
                    DetailRow(
                        label = stringResource(R.string.application_detail_date),
                        value = app.dateOfStart,
                    )
                    DetailRow(
                        label = stringResource(R.string.application_detail_coord_start),
                        value = app.coordStart,
                    )
                    DetailRow(
                        label = stringResource(R.string.application_detail_coord_end),
                        value = app.coordEnd,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .northStarSurfaceCard()
            .padding(14.dp),
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
