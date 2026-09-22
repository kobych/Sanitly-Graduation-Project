package io.github.kobych.sanitly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.kobych.sanitly.ui.theme.Dimens

@Composable
fun <T> CustomDropdownMenu(
    selected: String?,
    options: List<T>,
    onClick: (T) -> Unit,
    labelText: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .padding(Dimens.L.padding)
    ) {
        Row {
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(Icons.Default.Menu, contentDescription = "More goals")
            }
            if (selected != null) {
                Text(selected)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labelText(option)) },
                    onClick = { onClick(option) },
                )
            }
        }
    }
}
