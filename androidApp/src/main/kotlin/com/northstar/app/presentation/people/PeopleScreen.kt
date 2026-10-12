package com.northstar.app.presentation.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.data.api.UserDto
import com.northstar.app.presentation.components.NorthStarTabTopBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarTabTopBar(
            title = stringResource(R.string.tab_people),
            onThemeClick = { /* C17 */ },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            PeopleSearchPlaceholder()
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Ink)
                    }
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = uiState.errorMessage!!, color = MutedBrown)
                        TextButton(onClick = viewModel::load) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.users, key = { it.id }) { user ->
                            PeopleUserCard(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleSearchPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(CreamInput)
            .border(2.dp, Ink)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "⌕", fontWeight = FontWeight.Bold, color = Ink)
        Text(
            text = stringResource(R.string.people_search_hint),
            modifier = Modifier.padding(start = 8.dp),
            color = MutedBrown,
            fontSize = 14.sp,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun PeopleUserCard(user: UserDto) {
    val pending = user.cardAccentPending()
    val accent = if (pending) Amber else StatusGreen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(CreamInput)
            .border(1.dp, CardBorder),
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(accent),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.username,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Ink,
                    )
                    Text(
                        text = user.secondaryLine(),
                        fontSize = 13.sp,
                        color = MutedBrown,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (!user.carNumber.isNullOrBlank()) {
                    LicensePlate(number = user.carNumber!!)
                }
            }
            Text(
                text = user.status.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = accent,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
