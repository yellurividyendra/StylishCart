package com.geniusapk.shopping.domain.repo

import android.net.Uri
import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.BannerDataModels
import com.geniusapk.shopping.domain.models.CartDataModels
import com.geniusapk.shopping.domain.models.CategoryDataModels
import com.geniusapk.shopping.domain.models.FavDataModel
import com.geniusapk.shopping.domain.models.ProductDataModels
import com.geniusapk.shopping.domain.models.UserData
import com.geniusapk.shopping.domain.models.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun registerUserWithEmailAndPassword(userData: UserData):Flow<ResultState<String>>
    fun loginUserWithEmailAndPassword(userData: UserData):Flow<ResultState<String>>
    fun getuserById(uid:String): Flow<ResultState<UserDataParent>>
    fun upDateUserData(userDataParent: UserDataParent):Flow<ResultState<String>>
    fun userProfileImage(uri: Uri):Flow<ResultState<String>>
    fun getCategoriesInLimited (): Flow<ResultState<List<CategoryDataModels>>>
    fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>>
    fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>>
    fun getProductById(productId: String): Flow<ResultState<ProductDataModels>>
    fun addToCart(cartDataModels: CartDataModels):Flow<ResultState<String>>
    fun addtoFav(favDataModel: FavDataModel):Flow<ResultState<String>>
    fun unFav(itemID: String):Flow<ResultState<String>>
    fun getAllFav(): Flow<ResultState<List<FavDataModel>>>
    fun getCart(): Flow<ResultState<List<CartDataModels>>>
    fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>>
    //i am getting one problome about cart and fav , so i am anable to go cart screen to checkout screen

    fun getCheckout(productId: String): Flow<ResultState<ProductDataModels>>

    fun getBanner(): Flow<ResultState<List<BannerDataModels>>>

    fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>>

    fun deleteFromCart(itemID: String): Flow<ResultState<String>>
    fun searchProducts(query: String): Flow<ResultState<List<ProductDataModels>>>


}