package com.geniusapk.shopping.domain.models

import kotlinx.serialization.Serializable

data class CartDataModels (
    var productId: String = "",
    var name: String = "",
    var image: String = "",
    var price: String = "",
    var quantity: String = "",
    var cartId: String = "",
    var size: String = "",
    var description: String = "",
    var category: String = "",


)