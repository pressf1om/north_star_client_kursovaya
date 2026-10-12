package com.northstar.app.presentation.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.northstar.app.R
import com.northstar.app.data.api.CarDto
import com.northstar.app.data.api.UserDto
import com.northstar.app.presentation.components.NorthStarBackTopBar
import com.northstar.app.ui.components.NorthStarPrimaryButton
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CardBorder
import com.northstar.app.ui.theme.CreamInput
import com.northstar.app.ui.theme.Ink
import com.northstar.app.ui.theme.MutedBrown
import com.northstar.app.ui.theme.Navy

@Composable
fun AssignRoleScreen(
    onBack: () -> Unit,
    onAssigned: () -> Unit,
    viewModel: AssignRoleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NorthStarBackTopBar(
            title = stringResource(R.string.assign_role_title),
            onBack = onBack,
        )
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = Ink)
                }
            }
            uiState.user == null -> {
                Text(
                    text = uiState.errorMessage ?: "",
                    modifier = Modifier.padding(24.dp),
                    color = MutedBrown,
                )
            }
            else -> {
                AssignRoleBody(
                    user = uiState.user!!,
                    cars = uiState.cars,
                    selectedRole = uiState.selectedRole,
                    selectedCarId = uiState.selectedCarId,
                    isSaving = uiState.isSaving,
                    errorMessage = uiState.errorMessage,
                    onRoleSelected = viewModel::onRoleSelected,
                    onCarSelected = viewModel::onCarSelected,
                    onConfirm = { viewModel.confirm(onAssigned) },
                )
            }
        }
    }
}

@Composable
private fun AssignRoleBody(
    user: UserDto,
    cars: List<CarDto>,
    selectedRole: AssignableRole,
    selectedCarId: Int?,
    isSaving: Boolean,
    errorMessage: String?,
    onRoleSelected: (AssignableRole) -> Unit,
    onCarSelected: (Int) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        PendingUserSummaryCard(user = user)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.assign_role_label),
            fontWeight = FontWeight.ExtraBold,
            color = Ink,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoleChip(
                label = stringResource(R.string.role_dispatcher),
                selected = selectedRole == AssignableRole.DISPATCHER,
                onClick = { onRoleSelected(AssignableRole.DISPATCHER) },
                modifier = Modifier.weight(1f),
            )
            RoleChip(
                label = stringResource(R.string.role_driver),
                selected = selectedRole == AssignableRole.DRIVER,
                onClick = { onRoleSelected(AssignableRole.DRIVER) },
                modifier = Modifier.weight(1f),
            )
        }
        if (selectedRole == AssignableRole.DRIVER) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.assign_role_car_label),
                fontWeight = FontWeight.ExtraBold,
                color = Ink,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CarDropdown(
                cars = cars,
                selectedCarId = selectedCarId,
                onCarSelected = onCarSelected,
            )
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MutedBrown,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        NorthStarPrimaryButton(
            text = stringResource(R.string.assign_role_confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        )
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally),
                color = Ink,
            )
        }
    }
}

@Composable
private fun PendingUserSummaryCard(user: UserDto) {
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
                .background(Amber),
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = user.username, fontWeight = FontWeight.ExtraBold, color = Ink)
            Text(text = user.email, fontSize = 13.sp, color = MutedBrown)
            Text(
                text = user.status.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Amber,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun RoleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) Amber else Navy
    val background = if (selected) Amber.copy(alpha = 0.15f) else CreamInput
    Text(
        text = label.uppercase(),
        modifier = modifier
            .background(background)
            .border(2.dp, borderColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        color = Ink,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarDropdown(
    cars: List<CarDto>,
    selectedCarId: Int?,
    onCarSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = cars.find { it.id == selectedCarId }
    val label = selected?.let { "${it.carNumber} · ${it.model}" }
        ?: stringResource(R.string.assign_role_no_cars)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (cars.isNotEmpty()) expanded = !expanded },
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Navy,
                unfocusedBorderColor = Navy,
            ),
            enabled = cars.isNotEmpty(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            cars.forEach { car ->
                DropdownMenuItem(
                    text = { Text("${car.carNumber} · ${car.model}") },
                    onClick = {
                        onCarSelected(car.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
