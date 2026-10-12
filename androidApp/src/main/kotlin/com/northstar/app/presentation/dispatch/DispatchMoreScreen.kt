package com.northstar.app.presentation.dispatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.northstar.app.R
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.presentation.session.CurrentUser
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun DispatchMoreScreen(
    user: CurrentUser,
    onMessagesClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSignOut: () -> Unit,
    onThemeClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.tab_profile),
            onThemeClick = onThemeClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .northStarSurfaceCard()
                    .padding(14.dp),
            ) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink,
                )
                Text(
                    text = stringResource(R.string.role_dispatcher),
                    color = MutedBrown,
                )
            }
            DispatchMoreMenuItem(
                title = stringResource(R.string.dispatch_messages_menu),
                onClick = onMessagesClick,
            )
            DispatchMoreMenuItem(
                title = stringResource(R.string.analytics_menu),
                onClick = onAnalyticsClick,
            )
            DispatchMoreMenuItem(
                title = stringResource(R.string.about_menu),
                onClick = onAboutClick,
            )
            DispatchMoreMenuItem(
                title = stringResource(R.string.action_sign_out),
                onClick = onSignOut,
            )
        }
    }
}

@Composable
private fun DispatchMoreMenuItem(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .northStarSurfaceCard()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontWeight = FontWeight.ExtraBold, color = Ink, modifier = Modifier.weight(1f))
        Text(text = "›", color = Ink, fontWeight = FontWeight.Bold)
    }
}
