package com.northstar.app.presentation.admin

import androidx.compose.foundation.background
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
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun AdminMoreScreen(
    user: CurrentUser,
    onRoutesClick: () -> Unit,
    onTariffsClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onThemeClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.tab_more),
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
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.role_admin),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AdminMoreMenuItem(
                title = stringResource(R.string.admin_routes_menu),
                onClick = onRoutesClick,
            )
            AdminMoreMenuItem(
                title = stringResource(R.string.admin_tariffs_menu),
                onClick = onTariffsClick,
            )
            AdminMoreMenuItem(
                title = stringResource(R.string.analytics_menu),
                onClick = onAnalyticsClick,
            )
            AdminMoreMenuItem(
                title = stringResource(R.string.about_menu),
                onClick = onAboutClick,
            )
        }
    }
}

@Composable
private fun AdminMoreMenuItem(
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
        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(text = "›", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
