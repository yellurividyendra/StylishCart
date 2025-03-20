package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.CategoryDataModels
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(private val repo: Repo) {
    fun getAllCategoriesUseCase() : Flow<ResultState<List<CategoryDataModels>>> {
        return repo.getAllCategories()

    }
}