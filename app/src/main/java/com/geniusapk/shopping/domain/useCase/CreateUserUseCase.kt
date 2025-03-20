package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.UserData
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val repo: Repo) {
    fun createUser(userData: UserData) : Flow<ResultState<String>> {
        return repo.registerUserWithEmailAndPassword(userData)

    }


}