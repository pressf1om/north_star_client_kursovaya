package com.northstar.app.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CreamText
import com.northstar.app.ui.theme.NavInactive
import com.northstar.app.ui.theme.Navy

data class BottomTab(
    val route: String,
    val label: String,
)

@Composable
fun MainBottomBar(
    tabs: List<BottomTab>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBar(containerColor = Navy, tonalElevation = 0.dp) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(tab.route) },
                icon = {},
                label = {
                    Text(
                        text = tab.label.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Amber,
                    selectedTextColor = Amber,
                    unselectedIconColor = NavInactive,
                    unselectedTextColor = NavInactive,
                    indicatorColor = Navy,
                ),
            )
        }
    }
}
