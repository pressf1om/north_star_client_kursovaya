package com.northstar.app.presentation.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.northstar.app.BuildConfig
import com.northstar.app.R
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CreamSheet
import com.northstar.app.ui.theme.CreamText
import com.northstar.app.ui.theme.Navy

@Composable
fun PlaceholderScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy)
                .padding(horizontal = 20.dp, vertical = 36.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = CreamText,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.splash_subtitle).uppercase(),
                color = Amber,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = 0.dp)
                .background(CreamSheet)
                .border(2.dp, Navy)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.placeholder_nav_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            LicensePlate(number = "A777AA 71")
            Spacer(modifier = Modifier.height(16.dp))
            NorthStarPrimaryButton(text = "Войти")
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "API: ${BuildConfig.SERVER_BASE_URL}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
