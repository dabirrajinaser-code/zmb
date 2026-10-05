package com.example.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.WaterAqua600
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeCarousel(
    banners: List<String>,
    modifier: Modifier = Modifier
) {
    val effectiveBanners = if (banners.isEmpty()) {
        listOf("banner_pond", "banner_fry", "banner_tank", "banner_carp")
    } else {
        banners
    }

    val pagerState = rememberPagerState(pageCount = { effectiveBanners.size })
    val context = LocalContext.current

    // Auto-scroll effect
    LaunchedEffect(effectiveBanners.size) {
        while (effectiveBanners.size > 1) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % effectiveBanners.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
        }
    }

    LiquidGlossyCard(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val bannerKey = effectiveBanners[page]
                val imageModel: Any = when (bannerKey) {
                    "banner_pond" -> R.drawable.banner_pond
                    "banner_fry" -> R.drawable.banner_fry
                    "banner_tank" -> R.drawable.banner_tank
                    "banner_carp" -> R.drawable.banner_carp
                    else -> bannerKey // Can be content URI or file path or web URL
                }

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Hatchery Banner $page",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Carousel dots indicator
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(effectiveBanners.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(if (isSelected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) WaterAqua600 else Color.White.copy(alpha = 0.6f)
                            )
                    )
                }
            }
        }
    }
}
