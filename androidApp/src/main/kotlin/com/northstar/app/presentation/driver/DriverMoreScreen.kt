package com.northstar.app.presentation.driver

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
import androidx.compose.ui.unit.sp
import com.northstar.app.R
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.presentation.session.CurrentUser
import com.northstar.app.presentation.session.UserStatus
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown

@Composable
fun DriverMoreScreen(
    user: CurrentUser,
    onHelpClick: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.driver_cabinet_title),
            onThemeClick = { /* C17 */ },
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
                    .background(CreamInput)
                    .border(1.dp, CardBorder)
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.username,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Ink,
                        )
                        Text(
                            text = UserStatus.DRIVER,
                            color = MutedBrown,
                            fontSize = 13.sp,
                        )
                    }
                    if (!user.carNumber.isNullOrBlank()) {
                        LicensePlate(number = user.carNumber!!)
                    }
                }
            }
            DriverMoreMenuItem(
                title = stringResource(R.string.driver_help_menu),
                onClick = onHelpClick,
            )
            DriverMoreMenuItem(
                title = stringResource(R.string.driver_notes_menu),
                subtitle = stringResource(R.string.driver_notes_menu_sub),
                onClick = { /* C18 */ },
            )
            DriverMoreMenuItem(
                title = stringResource(R.string.action_sign_out),
                onClick = onSignOut,
            )
        }
    }
}

@Composable
private fun DriverMoreMenuItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(CreamInput)
            .border(1.dp, CardBorder)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.ExtraBold, color = Ink)
            if (subtitle != null) {
                Text(text = subtitle, color = MutedBrown, fontSize = 12.sp)
            }
        }
        Text(text = "›", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
