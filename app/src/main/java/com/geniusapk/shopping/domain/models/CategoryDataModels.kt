package com.geniusapk.shopping.domain.models

data class CategoryDataModels (
    var name: String = "",
    val date: Long = System.currentTimeMillis(),
    var createBy : String = "",
    var categoryImage : String = ""

)