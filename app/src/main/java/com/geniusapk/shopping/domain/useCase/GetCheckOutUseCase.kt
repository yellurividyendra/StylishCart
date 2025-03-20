package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.ProductDataModels
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCheckOutUseCase @Inject constructor(private val repo: Repo)  {

    fun getCheckoutUseCse(productId: String) : Flow<ResultState<ProductDataModels>> {
        return repo.getCheckout(productId)

    }
}