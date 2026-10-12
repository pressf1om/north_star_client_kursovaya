package com.northstar.app.presentation.dispatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.northstar.app.data.api.DriverMessageDto
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.presentation.components.NorthStarInlineSearchBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown

@Composable
fun DispatchMessagesScreen(
    onBack: () -> Unit,
    viewModel: DispatchMessagesViewModel = hiltViewModel(),
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
            title = stringResource(R.string.dispatch_messages_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            NorthStarInlineSearchBar(
                query = uiState.carFilter,
                onQueryChange = viewModel::onCarFilterChange,
                placeholder = stringResource(R.string.dispatch_messages_filter_hint),
                onSubmit = { viewModel.applyFilter() },
                onClear = { viewModel.clearFilter() },
            )
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 24.dp),
                        color = Ink,
                    )
                }
                uiState.errorMessage != null -> {
                    Text(uiState.errorMessage!!, color = MutedBrown)
                    TextButton(onClick = { viewModel.load() }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
                uiState.messages.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.dispatch_messages_empty),
                        color = MutedBrown,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items(uiState.messages, key = { it.id }) { message ->
                            DispatchMessageCard(message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DispatchMessageCard(message: DriverMessageDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(CreamInput)
            .border(1.dp, CardBorder)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LicensePlate(number = message.carNumber)
            Text(
                text = stringResource(R.string.dispatch_message_id, message.id),
                color = MutedBrown,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            text = message.message,
            color = Ink,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
