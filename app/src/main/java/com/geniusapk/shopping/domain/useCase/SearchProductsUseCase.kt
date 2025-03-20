package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.domain.repo.Repo
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(private val repo: Repo) {
    fun searchProducts(query: String) = repo.searchProducts(query)
}