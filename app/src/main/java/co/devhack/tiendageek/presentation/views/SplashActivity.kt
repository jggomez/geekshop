package co.devhack.tiendageek.presentation.views

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.devhack.tiendageek.R
import co.devhack.tiendageek.presentation.viewmodel.SignInViewModel
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.notify
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        signInViewModel = ViewModelProviders.of(
            this@SplashActivity
        ).get(SignInViewModel::class.java)

        initAnimation()
    }

    private fun initViewModel() {
        signInViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                startActivity(Intent(this, ProductsActivity::class.java))
            } else {
                startActivity(Intent(this, SignInActivity::class.java))
            }
        })

        signInViewModel.failure.observe(this, Observer {
            when (it) {
                Failure.NetworkConnection -> {
                    getString(R.string.lbl_network_connection).notify(this)
                }
                is Failure.ServerError -> {
                    it.ex.message?.notify(this)
                }
            }
        })

        signInViewModel.getCurrentUser()

    }

    private fun initAnimation() {
        val transition = AnimationUtils.loadAnimation(
            this,
            R.anim.splash_transition
        )
        imgGeekStore.animation = transition
        transition.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    initViewModel()
                }

                override fun onAnimationStart(animation: Animation?) {
                }

            }
        )
    }
}
