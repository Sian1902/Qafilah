package com.example.qafilah.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qafilah.R
import com.example.ui_kit.components.shared.PrimaryButton
import com.example.ui_kit.theme.QafilahTheme
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val leftImage: Int,
    @DrawableRes val topRightImage: Int,
    @DrawableRes val bottomRightImage: Int
)

private val pages = listOf(
    OnboardingPage(
        title = "Desert Elegance",
        description = "A refined collection of artisanal goods, delivered from the caravan to your door.",
        leftImage = R.drawable.onboarding_fabric,
        topRightImage = R.drawable.onboarding_ring,
        bottomRightImage = R.drawable.onboarding_bag
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp)
            ) { index ->
                ImageGrid(page = pages[index])
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = pages[pagerState.currentPage].title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = pages[pagerState.currentPage].description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text = if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Continue",
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
@Preview
@Composable
private fun OnboardingScreenPreview() {
    QafilahTheme {
        OnboardingScreen()
    }
}