package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.models.AnalyticsState
import io.github.kobych.sanitly.ui.models.states
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Typography
import io.github.kobych.sanitly.ui.viewmodels.AnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsFlowScreen(modifier: Modifier = Modifier) {
    val viewModel: AnalyticsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) { viewModel.getAllTasks() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.analytics_screen_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(horizontal = Dimens.L.padding)
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            PeriodFilterChip(onChangeState = { viewModel.updateAnalyticsState(it) }, uiState = uiState)
            Spacer(modifier = Modifier.height(Dimens.L.padding))

            when (uiState) {
                AnalyticsState.Day -> DayAnalyticsScreen(
                    state = viewModel.dayAnalyticsState,
                )

                AnalyticsState.Week -> WeekAnalyticsScreen(
                    state = viewModel.weekAnalyticsState
                )

                AnalyticsState.Month -> MonthAnalyticsScreen(
                    state = viewModel.monthAnalyticsState
                )
            }
        }
    }
}

@Composable
private fun PeriodFilterChip(
    uiState: AnalyticsState,
    onChangeState: (AnalyticsState) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding)
    ) {
        states.forEach { state ->
            FilterChip(
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
                onClick = {
                    onChangeState(state)
                },
                label = {
                    Text(
                        text = stringResource(state.titleId),
                        textAlign = TextAlign.Center,
                        style = Typography.labelLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                selected = state == uiState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BaseStatsGrid(
    states: List<BaseStatsGridState>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding),
            verticalArrangement = Arrangement.spacedBy(Dimens.S.padding),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(states) { info ->
                CardInfo(
                    state = info
                )
            }
        }
    }
}

@Composable
private fun CardInfo(
    state: BaseStatsGridState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(Dimens.L.padding)) {
            Text(text = stringResource(state.labelId), style = Typography.labelLarge)
            Spacer(modifier = Modifier.height(Dimens.S.padding))
            Text(text = state.cardInfo, style = Typography.bodyLarge)
        }
    }
}

data class BaseStatsGridState(
    val labelId: Int,
    val cardInfo: String,
)

@Preview(name = "Analytics Screen")
@Composable
fun AnalyticsFlowScreenPreview() {
    AnalyticsFlowScreen()
}
