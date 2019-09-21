package co.devhack.tiendageek.data.entities

import android.net.Uri

data class User(
    val uid: String,
    val nombre: String?,
    val email: String?,
    val urlPhoto: Uri?
)

data class Product(
    val id: String? = null,
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val description: String = "",
    val date: String = "",
    val active: Boolean = true
)