package co.devhack.tiendageek.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.devhack.tiendageek.data.entities.User
import co.devhack.tiendageek.data.repositories.UserRepository
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.NetworkHandler
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel(app: Application) : AndroidViewModel(app) {

    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val failure: MutableLiveData<Failure> by lazy {
        MutableLiveData<Failure>()
    }

    val userSignOut: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private val userRepository: UserRepository by lazy {
        UserRepository(
            NetworkHandler(app.applicationContext)
        )
    }

    fun sigInUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = userRepository.sigInUserWithEmailAndPassword(email, password)
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleUser)
            }
        }

    }

    fun sigInWithCredential(credential: AuthCredential) {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = userRepository.authUserWithCredentials(credential)
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleUser)
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = userRepository.getCurrentUserAuth()
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleUser)
            }
        }

    }

    fun signOut() =
        viewModelScope.launch(Dispatchers.IO) {
            val resp = userRepository.signOutUser()
            resp.either(::handleFailure, ::handleSignOut)
        }

    private fun handleSignOut(result: Boolean) {
        this.userSignOut.value = result
    }

    private fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

    private fun handleUser(user: User?) {
        this.user.value = user
    }

}