package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.CartDataModels
import com.geniusapk.shopping.domain.models.ProductDataModels
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddtoCardUseCase @Inject constructor(private val repo: Repo)  {
    fun addtoCard(cartDataModels : CartDataModels): Flow<ResultState<String>> {
        return repo.addToCart(cartDataModels)
    }
}