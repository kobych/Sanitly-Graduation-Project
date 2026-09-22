package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import kotlinx.coroutines.launch

@Composable
fun SurveyIntroductionScreen(
    onNext: () -> Unit,
    introductions: List<Introduction>,
    modifier: Modifier = Modifier
) {
    SurveyHorizontalPager(
        onNext = onNext,
        modifier = modifier,
        introductions = introductions
    )
}

@Composable
private fun SurveyHorizontalPager(
    onNext: () -> Unit,
    introductions: List<Introduction>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.L.padding)
    ) {

        val state = rememberPagerState(pageCount = { introductions.size })
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val bodyText = introductions[page].text

            val imageId = when (page) {
                0 -> R.drawable.introductionfirst
                1 -> R.drawable.introductionsecond
                2 -> R.drawable.introductionthird
                else -> null
            }

            PagerItem(
                pageNumber = page,
                bodyText = bodyText,
                imageId = imageId
            )
        }
        Button(
            onClick = {
                if (state.currentPage == introductions.lastIndex) {
                    onNext()
                } else {
                    coroutineScope.launch {
                        state.animateScrollToPage(state.currentPage + 1)
                    }
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .padding(bottom = Dimens.XL.padding)
                .size(Size.Button.XL.dp)
                .align(Alignment.BottomEnd)
        ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next state") }
    }
}

@Composable
private fun PagerItem(
    pageNumber: Int,
    bodyText: String,
    imageId: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.L.padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (pageNumber == 0) {
                Text(
                    text = stringResource(R.string.survey_introduction_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                if (imageId != null) {
                    Image(
                        painter = painterResource(imageId),
                        contentDescription = "Introduction"
                    )
                }
                Text(
                    text = bodyText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun SurveyIntroductionScreenPreview() {
    SurveyIntroductionScreen(
        onNext = {},
        introductions = emptyList()
    )
}
