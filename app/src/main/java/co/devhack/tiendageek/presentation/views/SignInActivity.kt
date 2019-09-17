package co.devhack.tiendageek.presentation.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.devhack.tiendageek.R
import co.devhack.tiendageek.presentation.viewmodel.SignInViewModel
import co.devhack.tiendageek.util.BaseActivity
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.notify
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var signInViewModel: SignInViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callBackManager: CallbackManager

    private val RC_SIGN_IN = 1010

    private enum class AuthType {
        GOOGLE,
        FACEBOOK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInViewModel = ViewModelProviders.of(
            this@SignInActivity
        ).get(SignInViewModel::class.java)

        btnSigIn.setOnClickListener {
            signIn()
        }

        btnSignInGoogle.setOnClickListener {
            mGoogleSignInClient = buildGoogleSignIn()
            val signIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signIntent, RC_SIGN_IN)
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        buildFacebookSignIn()

        signInViewModel.user.observe(this, Observer {
            hideProgress()
            Toast.makeText(
                this,
                getString(R.string.sigin_correct),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this, ProductsActivity::class.java))
            finish()
        })

        signInViewModel.failure.observe(this, Observer { failure ->
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

    private fun buildGoogleSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }

    private fun buildFacebookSignIn() {
        callBackManager = CallbackManager.Factory.create()
        btnSignInFacebook.registerCallback(callBackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    authenticateWithFirebaseCredential(
                        tokenFacebook = result?.accessToken,
                        authType = AuthType.FACEBOOK
                    )
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                    getString(R.string.lbl_error_auth_facebook).notify(
                        this@SignInActivity
                    )
                }

            })
    }

    private fun signIn() {
        val email = txt1.text.toString().trim()
        val password = txt2.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            getString(R.string.lbl_email_pass_mandatory).notify(this)
            return
        }

        showProgress()
        signInViewModel.sigInUserWithEmailAndPassword(email, password)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authenticateWithFirebaseCredential(
                    googleSignAccount = account,
                    authType = AuthType.GOOGLE
                )
            } catch (e: Exception) {
                e.message?.notify(this)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode != RC_SIGN_IN) {
            callBackManager.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun authenticateWithFirebaseCredential(
        googleSignAccount: GoogleSignInAccount? = null,
        tokenFacebook: AccessToken? = null,
        authType: AuthType
    ) {

        val credential = when (authType) {
            AuthType.GOOGLE -> GoogleAuthProvider.getCredential(
                googleSignAccount?.idToken, null
            )
            AuthType.FACEBOOK -> FacebookAuthProvider.getCredential(
                tokenFacebook?.token ?: ""
            )
        }

        signInViewModel.sigInWithCredential(credential)
        showProgress()
    }
}
