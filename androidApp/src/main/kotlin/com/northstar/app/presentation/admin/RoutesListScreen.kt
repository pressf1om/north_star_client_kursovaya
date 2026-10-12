package com.northstar.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.northstar.app.data.api.RouteDto
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun RoutesListScreen(
    onBack: () -> Unit,
    onAddRoute: () -> Unit,
    viewModel: RoutesListViewModel = hiltViewModel(),
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
            title = stringResource(R.string.admin_routes_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            NorthStarPrimaryButton(
                text = stringResource(R.string.admin_routes_add),
                onClick = onAddRoute,
                modifier = Modifier.fillMaxWidth(),
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
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { viewModel.load() }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
                uiState.routes.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.admin_routes_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                        items(uiState.routes, key = { it.id }) { route ->
                            RouteCard(route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteCard(route: RouteDto) {
    val kmLine = buildString {
        route.platonKm?.let { append("Платон $it км") }
        route.ollKm?.let {
            if (isNotEmpty()) append(" · ")
            append("Всего $it км")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .northStarSurfaceCard()
            .padding(14.dp),
    ) {
        Text(
            text = stringResource(R.string.admin_route_line, route.cityStart, route.cityEnd),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (kmLine.isNotEmpty()) {
            Text(
                text = kmLine,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (route.citiesOnRoute.isNotEmpty()) {
            Text(
                text = route.citiesOnRoute.joinToString(", "),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
