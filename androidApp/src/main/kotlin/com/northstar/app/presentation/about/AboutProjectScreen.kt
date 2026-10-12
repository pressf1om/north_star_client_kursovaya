package com.northstar.app.presentation.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.northstar.app.R
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun AboutProjectScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.about_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .northStarSurfaceCard()
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.about_heading),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.about_lead),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Text(
                text = stringResource(R.string.about_features_title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            AboutBullet(stringResource(R.string.about_feature_1))
            AboutBullet(stringResource(R.string.about_feature_2))
            AboutBullet(stringResource(R.string.about_feature_3))
            AboutBullet(stringResource(R.string.about_feature_4))
            AboutBullet(stringResource(R.string.about_feature_5))
            AboutBullet(stringResource(R.string.about_feature_6))
            Text(
                text = stringResource(R.string.about_footer),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun AboutBullet(text: String) {
    Text(
        text = "• $text",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
    )
}
