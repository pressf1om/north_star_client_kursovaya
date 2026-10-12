package com.northstar.app.presentation.driver

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.CreamText
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.Navy
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun DriverTripScreen(
    carNumber: String?,
    viewModel: DriverTripViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
        DriverTripTopBar(carNumber = carNumber)
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
                uiState.noTrip || uiState.application == null -> {
                    Text(
                        text = stringResource(R.string.driver_trip_empty),
                        color = MutedBrown,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
                else -> {
                    val app = uiState.application!!
                    TripCard(application = app)
                    Spacer(modifier = Modifier.height(16.dp))
                    val canAdvance = DriverTripStatusUi.nextStatusCode(app.status) != null
                    NorthStarPrimaryButton(
                        text = stringResource(R.string.driver_trip_confirm_step),
                        onClick = { viewModel.confirmStep() },
                        enabled = canAdvance && !uiState.isAdvancing,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedButton(
                        onClick = { copyCoordinates(context, app) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    ) {
                        Text(
                            stringResource(R.string.driver_trip_copy_coords),
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverTripTopBar(carNumber: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.tab_trip),
            color = CreamText,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
        )
        if (!carNumber.isNullOrBlank()) {
            LicensePlate(number = carNumber)
        }
    }
}

@Composable
private fun TripCard(application: ApplicationDto) {
    val status = application.status
    val weight = application.weight?.let { "$it т" } ?: ""
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CreamInput)
            .border(1.dp, CardBorder)
            .padding(14.dp),
    ) {
        Text(
            text = stringResource(R.string.driver_trip_application_title, application.id),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Ink,
        )
        Text(
            text = "${DriverTripStatusUi.statusLabel(status)} · $weight",
            color = MutedBrown,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        TripProgressBar(activeIndex = DriverTripStatusUi.progressIndex(status))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = DriverTripStatusUi.question(status),
            fontWeight = FontWeight.ExtraBold,
            color = Ink,
            fontSize = 15.sp,
        )
        Text(
            text = DriverTripStatusUi.nextHint(status),
            color = MutedBrown,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = stringResource(R.string.driver_trip_date_start, application.dateOfStart),
            color = MutedBrown,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun TripProgressBar(activeIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(7) { index ->
            val segment = index + 1
            val color = when {
                segment < activeIndex -> StatusGreen
                segment == activeIndex -> Amber
                else -> CardBorder
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(color, RoundedCornerShape(2.dp)),
            )
        }
    }
}

private fun copyCoordinates(context: Context, application: ApplicationDto) {
    val text = buildString {
        append("Начало: ")
        append(application.coordStart)
        append("\nКонец: ")
        append(application.coordEnd)
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("coords", text))
}
