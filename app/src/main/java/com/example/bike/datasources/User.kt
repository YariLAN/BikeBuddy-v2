package com.example.bike.datasources

import com.google.firebase.database.Exclude
import java.math.BigInteger
import java.security.MessageDigest

class User(var id: String, var firstName: String, var secondName: String, var email: String, var profileImage: String = "") {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "secondName" to secondName,
            "email" to email,
            "profileImage" to profileImage
        )
    }
}