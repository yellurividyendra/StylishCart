package com.geniusapk.shopping.presentation.screens.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.geniusapk.shopping.R
import com.geniusapk.shopping.domain.models.BannerDataModels
import com.geniusapk.shopping.ui.theme.SweetPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect as LaunchedEffect1


@Composable
fun Banner(
    banners: List<BannerDataModels>
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect1(Unit) {
        while (true) {
            delay(1500)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            scope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.wrapContentSize()
            ) { currentPage ->
                Card(
                    modifier = Modifier
                        .height(170.dp)
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 15.dp, end = 15.dp),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                ) {
                    AsyncImage(
                        model = banners[currentPage].image,
                        contentDescription = banners[currentPage].name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
            }
        }
        PageIndicator(
            pageCount = banners.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
        )
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) {
            IndicatorDot(isSelected = it == currentPage, modifier = modifier)
        }
    }
}

@Composable
fun IndicatorDot(isSelected: Boolean, modifier: Modifier) {
    if (isSelected)
        SelectedDot(modifier)
    else
        Box(
            modifier = modifier.padding(2.dp)
                .clip(shape = CircleShape)
                .size(8.dp)
                .background(color = SweetPink.copy(alpha = 0.5f), CircleShape)
        )
}

@Composable
fun SelectedDot(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .padding(2.dp)
            .height(10.dp)
            .width(28.dp)
            .background(SweetPink.copy(alpha = 0.8f), shape = RoundedCornerShape(5.dp)),
    )
}