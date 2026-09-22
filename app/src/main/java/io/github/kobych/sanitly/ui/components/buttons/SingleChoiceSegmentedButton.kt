package io.github.kobych.sanitly.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kobych.sanitly.ui.theme.Dimens

@Composable
fun <T> SingleChoiceSegmentedButton(
    options: List<T>,
    onClick: (T) -> Unit,
    labelText: (T) -> String,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = MaterialTheme.shapes.small
                ),
                onClick = {
                    selectedIndex = index
                    onClick(label)
                },
                selected = index == selectedIndex,
                label = {
                    Text(
                        labelText(label),
                    )
                },
                icon = {},
                modifier = Modifier.height(Dimens.XXL.padding)
            )
        }
    }
}
