package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFromCartUseCase @Inject constructor(private val repo: Repo) {

    fun deleteFromCart(itemID: String) : Flow<ResultState<String>> {
        return repo.deleteFromCart(itemID)

    }
}