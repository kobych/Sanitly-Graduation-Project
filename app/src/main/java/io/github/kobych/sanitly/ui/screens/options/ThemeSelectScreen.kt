package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun ThemeSelectScreen(
    selectedOption: UserPreferencesRepository.AppTheme,
    onSelected: (UserPreferencesRepository.AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        ThemeRadioButtons(
            selectedOption = selectedOption,
            onSelected = onSelected
        )
    }
}

@Composable
private fun ThemeRadioButtons(
    selectedOption: UserPreferencesRepository.AppTheme,
    onSelected: (UserPreferencesRepository.AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val radioOptions = UserPreferencesRepository.AppTheme.entries

    Column(
        modifier.selectableGroup()
    ) {
        radioOptions.forEach { theme ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.L.padding),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Size.Button.XL.dp)
                    .selectable(
                        selected = (theme == selectedOption),
                        onClick = { onSelected(theme) },
                        role = Role.RadioButton
                    )
                    .padding(Dimens.L.padding)
                    .testTag(theme.themeName)
            ) {
                RadioButton(
                    selected = (theme == selectedOption),
                    onClick = null
                )
                Text(
                    text = theme.themeName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(name = "Theme Select Screen")
@Composable
fun ThemeSelectScreenPreview() {
    ThemeSelectScreen(
        selectedOption = UserPreferencesRepository.AppTheme.SYSTEM,
        onSelected = {}
    )
}
