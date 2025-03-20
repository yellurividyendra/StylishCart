package com.geniusapk.shopping.data.repo

import android.net.Uri
import android.util.Log
import com.geniusapk.shopping.common.ADDTOFAV
import com.geniusapk.shopping.common.ADD_TO_CART
import com.geniusapk.shopping.common.PRODUCT_COLLECTION
import com.geniusapk.shopping.common.ResultState
import com.geniusapk.shopping.common.USER_COLLECTION
import com.geniusapk.shopping.domain.models.BannerDataModels
import com.geniusapk.shopping.domain.models.CartDataModels
import com.geniusapk.shopping.domain.models.CategoryDataModels
import com.geniusapk.shopping.domain.models.FavDataModel
import com.geniusapk.shopping.domain.models.ProductDataModels
import com.geniusapk.shopping.domain.models.UserData
import com.geniusapk.shopping.domain.models.UserDataParent
import com.geniusapk.shopping.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RepoImpl @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseFirestore: FirebaseFirestore
) : Repo {
    override fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)


            firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        firebaseFirestore.collection(USER_COLLECTION)
                            .document(it.result.user?.uid.toString()).set(userData)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    trySend(ResultState.Success("User Registered Successfully and add to Firestore"))
                                    updateFcmToken(firebaseAuth.currentUser?.uid.toString())

                                } else {
                                    if (it.exception != null) {
                                        trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                                    }
                                }
                            }

                        trySend(ResultState.Success("User Registered Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }


                }
            awaitClose {
                close()
            }

        }

    override fun loginUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseAuth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Logged In Successfully"))
                        updateFcmToken(firebaseAuth.currentUser?.uid.toString())

                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }

    override fun getuserById(uid: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(USER_COLLECTION)
            .document(uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.toObject(UserData::class.java)!!
                    val userDataParent = UserDataParent(it.result.id, data)
                    trySend(ResultState.Success(userDataParent))
                } else {
                    if (it.exception != null) {
                        trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                    }
                }
            }
        awaitClose {
            close()
        }
    }

    override fun upDateUserData(userDataParent: UserDataParent): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)

            firebaseFirestore.collection(USER_COLLECTION).document(userDataParent.nodeId)
                .update(userDataParent.userData.toMap())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Data Updated Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }

                    }
                }
            awaitClose {
                close()
            }

        }

    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseStorage.getInstance().reference.child("userProfileImages/${System.currentTimeMillis()}+${firebaseAuth.currentUser?.uid}")
            .putFile(uri ?: Uri.EMPTY).addOnCompleteListener {
                it.result.storage.downloadUrl.addOnSuccessListener { imageUrl ->
                    trySend(ResultState.Success(imageUrl.toString()))
                }
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }

            }
        awaitClose {
            close()
        }


    }

    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("categories").limit(5).get()
                .addOnSuccessListener { querySnapshot ->

                    val categories = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(CategoryDataModels::class.java)
                    }
                    trySend(ResultState.Success(categories))
                }
                .addOnFailureListener { exception ->
                    trySend(ResultState.Error(exception.toString()))
                }
            awaitClose { close() }

        }

    override fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").limit(10).get()
            .addOnSuccessListener {
                val products = it.documents.mapNotNull { document ->
                    document.toObject(ProductDataModels::class.java)?.apply {
                        productId = document.id
                    }
                }
                trySend(ResultState.Success(products))

            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()

        }

    }

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").get().addOnSuccessListener {
            val products = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                }

            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()

        }
    }

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(PRODUCT_COLLECTION).document(productId).get()
                .addOnSuccessListener {
                    val product = it.toObject(ProductDataModels::class.java)
                    trySend(ResultState.Success(product!!))
                    Log.d("test", "getProductById: $product")
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }

        }

    override fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(ADD_TO_CART).document(firebaseAuth.currentUser!!.uid)
                .collection(
                    "User_Cart"
                )

                .add(cartDataModels).addOnSuccessListener {
                    //  cartDataModels.cartId = it.id
                    trySend(ResultState.Success("Product Added To Cart"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }

        }

    override fun addtoFav(favDataModel: FavDataModel): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
                .collection(
                    "User_Fav"

                ).add(favDataModel)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product Added To Fav"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }

        }

    override fun unFav(itemID: String): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
                .collection("User_Fav").document(itemID).delete().addOnSuccessListener {
                    trySend(ResultState.Success("Product Removed From Fav"))
                    Log.d("test", "unFav: $itemID")

                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                    Log.d("test", "unFav: ${it.toString()}")

                }
            awaitClose {
                close()
            }

        }

    override fun getAllFav(): Flow<ResultState<List<FavDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid)
            .collection("User_Fav").get().addOnSuccessListener {
                val fav = it.documents.mapNotNull { document ->
                    document.toObject(FavDataModel::class.java)?.apply {
                        favId = document.id

                    }
                }
                trySend(ResultState.Success(fav))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()

        }

    }

    override fun getCart(): Flow<ResultState<List<CartDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADD_TO_CART).document(firebaseAuth.currentUser!!.uid)
            .collection("User_Cart").get().addOnSuccessListener {
                val cart = it.documents.mapNotNull { document ->
                    document.toObject(CartDataModels::class.java)?.apply {
                        cartId = document.id

                    }
                }
                trySend(ResultState.Success(cart))
            }
        awaitClose {
            close()
        }


    }

    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("categories").get().addOnSuccessListener {
            val categories = it.documents.mapNotNull { document ->
                document.toObject(CategoryDataModels::class.java)
            }
            trySend(ResultState.Success(categories))

        }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()
        }

    }

    override fun getCheckout(productId: String): Flow<ResultState<ProductDataModels>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Products").document(productId).get()
                .addOnSuccessListener {
                    val product = it.toObject(ProductDataModels::class.java)
                    trySend(ResultState.Success(product!!))

                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }

        }

    override fun getBanner(): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {

        trySend(ResultState.Loading)



        firebaseFirestore.collection("banner").get().addOnSuccessListener {

            val banner = it.documents.mapNotNull { document ->
                document.toObject(BannerDataModels::class.java)
            }
            trySend(ResultState.Success(banner))
        }
            .addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()

        }
    }

    override fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Products").whereEqualTo("category", categoryName).get()
                .addOnSuccessListener {
                    val products = it.documents.mapNotNull { document ->
                        document.toObject(ProductDataModels::class.java)?.apply {
                            productId = document.id
                        }
                    }
                    trySend(ResultState.Success(products))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
            }

        }

    override fun deleteFromCart(itemID: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection(ADD_TO_CART).document(firebaseAuth.currentUser!!.uid)
            .collection("User_Cart").document(itemID).delete().addOnSuccessListener {
                trySend(ResultState.Success("Item Deleted From Cart"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {

        }
    }

    override fun searchProducts(query: String): Flow<ResultState<List<ProductDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Products") .orderBy("name")
                .startAt(query).get()
                .addOnSuccessListener {
                    val products = it.documents.mapNotNull { document ->
                        document.toObject(ProductDataModels::class.java)?.apply {
                            productId = document.id
                        }
                    }
                    trySend(ResultState.Success(products))
                }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
            awaitClose {
                close()
            }
        }




    // this is test , i am takeing user tocken in firebase
    fun updateFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firebaseFirestore.collection("user_tokens").document(userId)
                    .set(mapOf("token" to token))
            }
        }
    }

}



