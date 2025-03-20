package com.geniusapk.shopping.domain.models




data class UserData(
    val fastName : String = "",
    val lastName : String = "",
    val email : String = "",
    val password : String = "",
    val phoneNumber : String = "",
    val address : String = "",
    val profileImage : String = ""

){
    // Convert UserData object to a map for Firestore
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["fastName"] = fastName
        map["lastName"] = lastName
        map["email"] = email
        map["password"] = password
        map["phoneNumber"] = phoneNumber
        map["address"] = address
        map["profileImage"] = profileImage
        return map
    }
}


data class UserDataParent(val nodeId : String = "", val userData: UserData = UserData())