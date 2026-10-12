package com.northstar.app.presentation.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.northStarSurfaceCard
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun ArchiveScreen(
    carNumber: String?,
    onThemeClick: () -> Unit,
    viewModel: ArchiveViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, carNumber) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load(carNumber)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.load(carNumber)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.archive_title),
            onThemeClick = onThemeClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Ink,
                    )
                }
                uiState.errorMessage != null -> {
                    Text(uiState.errorMessage!!, color = MutedBrown)
                    TextButton(onClick = { viewModel.load(carNumber) }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
                uiState.items.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.archive_empty),
                        color = MutedBrown,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
                else -> {
                    LazyColumn {
                        items(uiState.items, key = { it.id }) { item ->
                            ArchiveTripCard(application = item, showPlate = carNumber == null)
                            Spacer(modifier = Modifier.padding(bottom = 10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveTripCard(
    application: ApplicationDto,
    showPlate: Boolean,
) {
    val weight = application.weight?.let { "$it т" } ?: ""
    val subtitle = buildString {
        append(application.dateOfStart)
        if (weight.isNotEmpty()) {
            append(" · ")
            append(weight)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .northStarSurfaceCard()
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showPlate && !application.carNow.isNullOrBlank()) {
                LicensePlate(number = application.carNow!!)
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.archive_application_title, application.id),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        Text(
            text = stringResource(R.string.archive_status_completed),
            color = StatusGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
