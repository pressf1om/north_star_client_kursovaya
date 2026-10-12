package com.northstar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.PlateYellow

@Composable
fun LicensePlate(
    number: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(PlateYellow)
            .border(2.dp, Ink)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = number,
            color = Ink,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun NorthStarPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Ink,
        ),
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
        )
    }
}
