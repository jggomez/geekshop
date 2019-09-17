package co.devhack.tiendageek.util

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun String.notify(context: AppCompatActivity) {

    Snackbar.make(
        context.findViewById(android.R.id.content),
        this,
        Snackbar.LENGTH_SHORT
    ).show()
}