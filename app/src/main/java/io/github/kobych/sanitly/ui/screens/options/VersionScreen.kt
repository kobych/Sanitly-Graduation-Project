package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens

@Composable
fun VersionScreen(
    version: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.L.padding),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = version,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(name = "Version Screen")
@Composable
fun VersionScreenPreview() {
    VersionScreen(
        version = "1.0"
    )
}
