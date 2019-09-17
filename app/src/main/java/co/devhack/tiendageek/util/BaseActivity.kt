package co.devhack.tiendageek.util

import androidx.appcompat.app.AppCompatActivity
import co.devhack.tiendageek.R
import com.afollestad.materialdialogs.MaterialDialog


open class BaseActivity : AppCompatActivity() {

    private var progress: MaterialDialog? = null

    internal fun showProgress() = progressStatus(true)

    internal fun hideProgress() = progressStatus(false)

    private fun progressStatus(visible: Boolean) {

        when (visible) {
            true -> {
                progress = MaterialDialog(this).show {
                    title(res = R.string.lbl_processing)
                    message(res = R.string.lbl_processing_description)
                }
            }

            false -> progress?.dismiss()
        }

    }
}

