package com.geniusapk.shopping.domain.useCase

import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.UserDataParent
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor( val repo: Repo) {

    fun getuserById(uid:String) : Flow<ResultState<UserDataParent>> {
        return repo.getuserById(uid)
    }
}