package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Result
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun SurveyResultsScreen(
    result: Result?,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.L.padding),
                modifier = modifier.padding(Dimens.L.padding)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.survey_results_verdict_label),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground
                )

                ResultsCard(result.verdict)

                Text(
                    text = stringResource(R.string.survey_results_advantages_label),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground
                )
                result.advantages.forEach { advantage ->
                    ResultsCard(advantage)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onFinish() },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .padding(bottom = Dimens.XL.padding)
                        .height(Size.Button.XL.dp)
                        .fillMaxWidth()
                ) { Text(stringResource(R.string.survey_results_start_btn)) }
            }
        }
    }
}

@Composable
private fun ResultsCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(Dimens.L.padding)
        )
    }
}

@Preview
@Composable
fun SurveyResultsScreenPreview() {
    SurveyResultsScreen(
        result = null,
        onFinish = {},
    )
}
