package com.geniusapk.shopping.presentation.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geniusapk.shopping.common.HomeScreenState
import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.domain.models.BannerDataModels
import com.geniusapk.shopping.domain.models.CartDataModels
import com.geniusapk.shopping.domain.models.CategoryDataModels
import com.geniusapk.shopping.domain.models.FavDataModel
import com.geniusapk.shopping.domain.models.ProductDataModels
import com.geniusapk.shopping.domain.models.UserData
import com.geniusapk.shopping.domain.models.UserDataParent
import com.geniusapk.shopping.domain.useCase.AddToFavUseCase
import com.geniusapk.shopping.domain.useCase.AddtoCardUseCase
import com.geniusapk.shopping.domain.useCase.CreateUserUseCase
import com.geniusapk.shopping.domain.useCase.DeleteFromCartUseCase
import com.geniusapk.shopping.domain.useCase.GetAllCategoriesUseCase
import com.geniusapk.shopping.domain.useCase.GetAllFavUseCase
import com.geniusapk.shopping.domain.useCase.GetAllProductUseCase
import com.geniusapk.shopping.domain.useCase.GetBannerUseCase
import com.geniusapk.shopping.domain.useCase.GetCartUseCase
import com.geniusapk.shopping.domain.useCase.GetCheckOutUseCase
import com.geniusapk.shopping.domain.useCase.GetSpecifiCategoryItems
import com.geniusapk.shopping.domain.useCase.GetUserUseCase
import com.geniusapk.shopping.domain.useCase.LoginUserUseCase
import com.geniusapk.shopping.domain.useCase.SearchProductsUseCase
import com.geniusapk.shopping.domain.useCase.UnfavUseCase
import com.geniusapk.shopping.domain.useCase.UpDateUserDataUseCase
import com.geniusapk.shopping.domain.useCase.getCategoryInLimit
import com.geniusapk.shopping.domain.useCase.getProductByID
import com.geniusapk.shopping.domain.useCase.getProductsInLimitUseCase
import com.geniusapk.shopping.domain.useCase.userProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.debounce
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ShoppingAppViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val upDateUserDataUseCase: UpDateUserDataUseCase,
    private val userProfileImageUseCase: userProfileImageUseCase,
    private val getCategoryInLimit: getCategoryInLimit,
    private val getProductsInLimitUseCase: getProductsInLimitUseCase,
    private val addtoCardUseCase: AddtoCardUseCase,
    private val getProductByID: getProductByID,
    private val addtoFavUseCase: AddToFavUseCase,
    private val getAllFavUseCase: GetAllFavUseCase,
    private val getAllProductsUseCase: GetAllProductUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getCheckOutUseCase : GetCheckOutUseCase,
    private val getBannerUseCase: GetBannerUseCase,
    private val getSpecifiCategoryItems: GetSpecifiCategoryItems,
    private val deleteFromCartUseCase: DeleteFromCartUseCase,
    private val unFavUseCase: UnfavUseCase,
    private val SearchProductsUseCase: SearchProductsUseCase
    ) : ViewModel() {

    private val _singUpScreenState = MutableStateFlow(SignUpScreenState())
    val singUpScreenState = _singUpScreenState.asStateFlow()



    private val _loginScreenState = MutableStateFlow(LoginScreenState())
    val loginScreenState = _loginScreenState.asStateFlow()

    private val _profileScreenState = MutableStateFlow(ProfileScreenState())
    val profileScreenState = _profileScreenState.asStateFlow()

    private val _upDateScreenState = MutableStateFlow(UpDateScreenState())
    val upDateScreenState = _upDateScreenState.asStateFlow()

    private val _userProfileImageState = MutableStateFlow(uploadUserProfileImageState())
    val userProfileImageState = _userProfileImageState.asStateFlow()


    //how to use this , when usr click on button
    private val _addToCartState = MutableStateFlow(AddtoCardState())
    val addToCartState = _addToCartState.asStateFlow()

    private val _getProductByIDState = MutableStateFlow(GetProductByIDState())
    val getProductByIDState = _getProductByIDState.asStateFlow()

    //how to use this , when usr click on button
    private val _addtoFavState = MutableStateFlow(AddtoFavState())
    val addtoFavState = _addtoFavState.asStateFlow()

    private val _getAllFavState = MutableStateFlow(GetAllFavState())
    val getAllFavState = _getAllFavState.asStateFlow()

    private val _getAllProductsState = MutableStateFlow(GetAllProductsState())
    val getAllProductsState = _getAllProductsState.asStateFlow()


    private val _getCartState = MutableStateFlow(GetCartsState())
    val getCartState = _getCartState.asStateFlow()


    private val _getAllCategoriesState = MutableStateFlow(GetAllCategoriesState())
    val getAllCategoriesState = _getAllCategoriesState.asStateFlow()



    private val _getCheckOutState = MutableStateFlow(GetCheckOutState())
    val getCheckOutState = _getCheckOutState.asStateFlow()


    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()



    private val _getSpecifiCategoryItemsState = MutableStateFlow(GetSpecifiCategoryItemsState())
    val getSpecifiCategoryItemsState = _getSpecifiCategoryItemsState.asStateFlow()

    private val _deleteFromCartState = MutableStateFlow(DeleteFromCartState())
    val deleteFromCartState = _deleteFromCartState.asStateFlow()

    private val _unFavState = MutableStateFlow(UnFavState())
    val unFavState = _unFavState.asStateFlow()

     val _searchQuery = MutableStateFlow("") // Query state flow

    private val _searchProductsState = MutableStateFlow(SearchProductsState())
    val searchProductsState = _searchProductsState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }


    fun searchQuery(){
        viewModelScope.launch(context = Dispatchers.IO) {
            _searchQuery
                .debounce(500L) // 500ms debounce time
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        searchProducts(query)
                    }
                }
        }
    }

    fun searchProducts(query: String){
        Log.d("Search", "Searching for: $query")

        viewModelScope.launch(Dispatchers.IO) {
            SearchProductsUseCase.searchProducts(query).collect{
                when(it){
                    is ResultState.Error -> {
                        _searchProductsState.value = _searchProductsState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _searchProductsState.value = _searchProductsState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        Log.d("Search", "Search results: ${it.data}")
                        _searchProductsState.value = _searchProductsState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }


    fun unFav(itemID: String){
        viewModelScope.launch(Dispatchers.IO) {
            unFavUseCase.unfav(itemId = itemID).collect{
                when(it){
                    is ResultState.Error -> {
                        _unFavState.value = _unFavState.value.copy(
                            isLoading = false,
                            errorMessage = it.message)
                    }
                    is ResultState.Loading -> {
                        _unFavState.value = _unFavState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _unFavState.value = _unFavState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }

    fun deleteFromCart(itemID: String){
        viewModelScope.launch(Dispatchers.IO) {
        deleteFromCartUseCase.deleteFromCart(itemID).collect{
            when(it){
                is ResultState.Error -> {
                    _deleteFromCartState.value = _deleteFromCartState.value.copy(
                        isLoading = false,
                        errorMessage = it.message
                    )
                }
                is ResultState.Loading -> {
                    _deleteFromCartState.value = _deleteFromCartState.value.copy(
                        isLoading = true
                    )
                }
                is ResultState.Success -> {
                    _deleteFromCartState.value = _deleteFromCartState.value.copy(
                        isLoading = false,
                        userData = it.data
                    )
                }
            }
        }
        }
    }



    fun getSpecifiCategoryItems(
        categoryName : String
    ){
        viewModelScope.launch(Dispatchers.IO) {
            getSpecifiCategoryItems.getSpecifiCategoryItems(categoryName).collect{
                when(it){
                    is ResultState.Error -> {
                        _getSpecifiCategoryItemsState.value = _getSpecifiCategoryItemsState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getSpecifiCategoryItemsState.value = _getSpecifiCategoryItemsState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getSpecifiCategoryItemsState.value = _getSpecifiCategoryItemsState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }





    fun getCheckOut(productId: String){
        viewModelScope.launch {
            getCheckOutUseCase.getCheckoutUseCse(productId).collect{
                when(it){
                    is ResultState.Error -> {
                        _getCheckOutState.value = _getCheckOutState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getCheckOutState.value = _getCheckOutState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getCheckOutState.value = _getCheckOutState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }

            }
        }
    }





    fun getAllCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesUseCase.getAllCategoriesUseCase().collect{
                when(it){
                    is ResultState.Error -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }

            }
        }
    }


    fun getCart(){
        viewModelScope.launch(Dispatchers.IO) {
            getCartUseCase.getCart().collect {
                when(it){
                    is ResultState.Error -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }



    fun getAllProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsUseCase.getAllProduct().collect{
                when(it){
                    is ResultState.Loading -> {
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Error -> {
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Success ->{
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }

        }
    }



    fun getAllFav(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllFavUseCase.getAllFav().collect{
                when(it){
                    is ResultState.Error -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }



    fun addToFav(favDataModel : FavDataModel){
        viewModelScope.launch(Dispatchers.IO) {
            addtoFavUseCase.addtoFav(favDataModel).collect{
                when(it){
                    is ResultState.Error -> {
                        _addtoFavState.value = _addtoFavState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _addtoFavState.value = _addtoFavState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _addtoFavState.value = _addtoFavState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }





    fun getProductByID(productId: String){
        viewModelScope.launch(Dispatchers.IO) {
            getProductByID.getProductById(productId).collect{
                when(it){
                    is ResultState.Error -> {
                        _getProductByIDState.value = _getProductByIDState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getProductByIDState.value = _getProductByIDState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getProductByIDState.value = _getProductByIDState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }

        }
    }


    fun addToCart(
        cartDataModels: CartDataModels
    ){
        viewModelScope.launch(Dispatchers.IO) {
            addtoCardUseCase.addtoCard( cartDataModels ).collect{
                when(it){
                    is ResultState.Error -> {
                        _addToCartState.value = _addToCartState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _addToCartState.value = _addToCartState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _addToCartState.value = _addToCartState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )

                    }
                }
            }
        }
    }




    init {
        loadHomeScreenData()
        searchQuery()

    }


//    private val _homeScreenState = MutableStateFlow(HomeScreenState())
//    val homeScreenState = _homeScreenState.asStateFlow()
//


    fun loadHomeScreenData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                getCategoryInLimit.getCategoriesInLimited(),
                getProductsInLimitUseCase.getProductsInLimit(),
                getBannerUseCase.getBannerUseCase()
            ) { categoriesResult, productsResult  , bannerResult->
                when {
                    categoriesResult is ResultState.Error ->
                        HomeScreenState(isLoading = false, errorMessage = categoriesResult.message)
                    productsResult is ResultState.Error ->
                        HomeScreenState(isLoading = false, errorMessage = productsResult.message)
                    bannerResult is ResultState.Error ->
                        HomeScreenState(isLoading = false, errorMessage = bannerResult.message)

                    categoriesResult is ResultState.Success &&
                            productsResult is ResultState.Success && bannerResult is ResultState.Success
                             ->
                        HomeScreenState(
                            isLoading = false,
                            categories = categoriesResult.data,
                            products = productsResult.data,
                            banners = bannerResult.data
                        )
                    else -> HomeScreenState(isLoading = true)
                }
            }.collect { state ->
                _homeScreenState.value = state
            }
        }
    }






    fun upLoadUserProfileImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            userProfileImageUseCase.userProfileImage(uri).collect {
                when (it) {
                    is ResultState.Error -> {
                        _userProfileImageState.value = _userProfileImageState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _userProfileImageState.value = _userProfileImageState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _userProfileImageState.value = _userProfileImageState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }


    fun upDateUserData(
        userDataParent: UserDataParent
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            upDateUserDataUseCase.upDateUserData(userDataParent = userDataParent).collect {
                when (it) {
                    is ResultState.Error -> {
                        _upDateScreenState.value = _upDateScreenState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _upDateScreenState.value = _upDateScreenState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _upDateScreenState.value = _upDateScreenState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )

                    }
                }
            }
        }
    }


    fun createUser(userData: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            createUserUseCase.createUser(userData).collect {
                when (it) {
                    is ResultState.Error -> {
                        _singUpScreenState.value = _singUpScreenState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    ResultState.Loading -> {
                        _singUpScreenState.value = _singUpScreenState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _singUpScreenState.value = _singUpScreenState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }

        }
    }


    fun loginUser(userData: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUserUseCase.loginUser(userData).collect {
                when (it) {
                    is ResultState.Error -> {
                        _loginScreenState.value = _loginScreenState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    ResultState.Loading -> {
                        _loginScreenState.value = _loginScreenState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _loginScreenState.value = _loginScreenState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }

        }
    }


    fun getUserById(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase.getuserById(uid).collectLatest {
                when (it) {
                    is ResultState.Error -> {
                        _profileScreenState.value = _profileScreenState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )

                    }

                    ResultState.Loading -> {
                        _profileScreenState.value = _profileScreenState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _profileScreenState.value = _profileScreenState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }


}

data class ProfileScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: UserDataParent? = null
)


data class SignUpScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: String? = null

)

data class LoginScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: String? = null
)

data class UpDateScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: String? = null
)

data class uploadUserProfileImageState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: String? = null
)


data class AddtoCardState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: String? = null

)

data class GetProductByIDState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: ProductDataModels? = null

)

data class AddtoFavState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    var userData: String? = null

)


data class GetAllFavState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<FavDataModel?> = emptyList()

)


data class GetAllProductsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<ProductDataModels?> = emptyList()

)


data class GetCartsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<CartDataModels?> = emptyList()

)


data class GetAllCategoriesState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<CategoryDataModels?> = emptyList()

)


data class GetCheckOutState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: ProductDataModels? = null

)

data class GetSpecifiCategoryItemsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<ProductDataModels?> = emptyList()

)



data class DeleteFromCartState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    var userData: String? = null

)

data class UnFavState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    var userData: String? = null

)

data class SearchProductsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: List<ProductDataModels?> = emptyList()

)






