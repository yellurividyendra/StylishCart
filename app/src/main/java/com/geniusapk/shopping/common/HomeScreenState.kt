package com.geniusapk.shopping.common

import com.geniusapk.shopping.domain.models.BannerDataModels
import com.geniusapk.shopping.domain.models.CategoryDataModels
import com.geniusapk.shopping.domain.models.ProductDataModels

data class HomeScreenState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val categories: List<CategoryDataModels>? = null,
    val products: List<ProductDataModels>? = null,
    val banners: List<BannerDataModels>? = null
)