package com.geniusapk.shopping.domain.useCase

import android.net.Uri
import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class userProfileImageUseCase @Inject constructor(private val repo: Repo)  {
    fun userProfileImage(uri: Uri) : Flow<ResultState<String>> {
        return repo.userProfileImage(uri)
    }

}