package com.northstar.app.presentation.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.northstar.app.domain.notes.CarNote
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.LicensePlate
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.northStarOutlinedFieldColors
import com.northstar.app.ui.theme.northStarSurfaceCard

@Composable
fun DriverNotesScreen(
    carNumber: String?,
    onBack: () -> Unit,
    viewModel: DriverNotesViewModel = hiltViewModel(),
) {
    val draft by viewModel.draftText.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.driver_notes_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            if (!carNumber.isNullOrBlank()) {
                LicensePlate(number = carNumber)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = stringResource(R.string.driver_notes_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
            if (notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.driver_notes_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(note)
                    }
                }
            }
            OutlinedTextField(
                value = draft,
                onValueChange = viewModel::onDraftChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (notes.isEmpty()) 12.dp else 0.dp),
                placeholder = { Text(stringResource(R.string.driver_notes_hint)) },
                colors = northStarOutlinedFieldColors(),
            )
            NorthStarPrimaryButton(
                text = stringResource(R.string.driver_notes_add),
                onClick = viewModel::addNote,
                enabled = draft.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
            if (notes.isNotEmpty()) {
                TextButton(
                    onClick = viewModel::clearAll,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = stringResource(R.string.driver_notes_clear_all),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: CarNote) {
    Text(
        text = note.text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .northStarSurfaceCard()
            .padding(12.dp),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
    )
}
