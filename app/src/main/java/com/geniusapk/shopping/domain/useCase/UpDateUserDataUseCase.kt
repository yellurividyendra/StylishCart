package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.UserDataParent
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpDateUserDataUseCase @Inject constructor(private val repo: Repo)  {
    fun upDateUserData(userDataParent: UserDataParent) : Flow<ResultState<String>> {
        return repo.upDateUserData(userDataParent)

    }

}