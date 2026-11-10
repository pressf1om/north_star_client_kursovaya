package com.northstar.app.presentation.driver

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.data.api.DriverMessageDto
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.northStarOutlinedFieldColors
import com.northstar.app.ui.theme.northStarSurfaceCard
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun DriverHelpScreen(
    carNumber: String?,
    onBack: () -> Unit,
    viewModel: DriverHelpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(carNumber) {
        viewModel.loadHistory(carNumber)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.driver_help_title),
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
                text = stringResource(R.string.driver_help_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                text = stringResource(R.string.driver_help_car_label),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (!carNumber.isNullOrBlank()) {
                LicensePlate(number = carNumber)
            } else {
                Text(
                    stringResource(R.string.driver_help_no_car),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (uiState.history.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.driver_help_history),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                uiState.history.forEach { item ->
                    DriverMessageHistoryCard(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.driver_help_message_label),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
            )
            OutlinedTextField(
                value = uiState.messageText,
                onValueChange = viewModel::onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(120.dp),
                placeholder = { Text(stringResource(R.string.driver_help_message_hint)) },
                colors = northStarOutlinedFieldColors(),
            )
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (uiState.sentSuccess) {
                Text(
                    text = stringResource(R.string.driver_help_sent),
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            NorthStarPrimaryButton(
                text = stringResource(R.string.driver_help_send),
                onClick = { viewModel.send(carNumber) },
                enabled = !uiState.isSending && !carNumber.isNullOrBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun DriverMessageHistoryCard(item: DriverMessageDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .northStarSurfaceCard()
            .padding(12.dp),
    ) {
        Text(
            text = stringResource(R.string.driver_help_you),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = item.message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (!item.reply.isNullOrBlank()) {
            Text(
                text = stringResource(R.string.driver_help_dispatcher),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = item.reply,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
            )
        } else {
            Text(
                text = stringResource(R.string.driver_help_waiting_reply),
                fontSize = 12.sp,
                color = MutedBrown,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
