package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.models.OptionsState
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun OptionsSelectScreen(
    onClick: (OptionsState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.XL.padding),
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        OptionsItemCard(
            cardTitleId = R.string.options_notifications_title,
            onClick = { onClick(OptionsState.Notifications) }
        )
        OptionsItemCard(
            cardTitleId = R.string.options_theme_title,
            onClick = { onClick(OptionsState.Theme) }
        )
        OptionsItemCard(
            cardTitleId = R.string.options_about_title,
            onClick = { onClick(OptionsState.About) }
        )
    }
}

@Composable
private fun OptionsItemCard(
    cardTitleId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Size.Button.XL.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(cardTitleId),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(Dimens.L.padding)
            )
        }
    }
}

@Preview(name = "Options Select Screen")
@Composable
fun OptionsSelectScreenPreview() {
    OptionsSelectScreen(
        onClick = {}
    )
}
