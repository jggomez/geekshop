package co.devhack.tiendageek.presentation.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.devhack.tiendageek.R
import co.devhack.tiendageek.presentation.viewmodel.SignUpViewModel
import co.devhack.tiendageek.util.BaseActivity
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.notify
import kotlinx.android.synthetic.main.activity_main.*

class SignUpActivity : BaseActivity() {

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signUpViewModel =
            ViewModelProviders.of(this@SignUpActivity)
                .get(SignUpViewModel::class.java)

        btnSignUp.setOnClickListener {
            signUp()
        }

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signUpViewModel.user.observe(this, Observer {
            hideProgress()
            startActivity(Intent(this, SignInActivity::class.java))
            Toast.makeText(
                this,
                getString(R.string.account_created),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        })

        signUpViewModel.failure.observe(this, Observer { failure ->
            hideProgress()
            when (failure) {
                Failure.NetworkConnection -> {
                    getString(R.string.lbl_network_connection).notify(this)
                }
                is Failure.ServerError -> {
                    failure.ex.message?.notify(this)
                }
            }
        })
    }

    private fun signUp() {
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        val passwordRepeat = txtPasswordRepeat.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            getString(R.string.lbl_email_pass_mandatory).notify(this)
            return
        }

        if (password != passwordRepeat) {
            getString(R.string.lbl_password_incorrecto).notify(this)
            return
        }

        showProgress()

        signUpViewModel.createUserWithEmailAndPassword(email, password)

    }
}
