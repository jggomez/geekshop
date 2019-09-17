package co.devhack.tiendageek.data.entities

import android.net.Uri
import com.google.firebase.firestore.FieldValue

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
    var date: String = ""
)