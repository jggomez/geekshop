package co.devhack.tiendageek.data.entities

import android.net.Uri

data class User(
    val uid: String,
    val nombre: String?,
    val email: String?,
    val urlPhoto: Uri?
)