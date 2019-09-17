package co.devhack.tiendageek.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.devhack.tiendageek.data.entities.User
import co.devhack.tiendageek.data.repositories.UserRepository
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.NetworkHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel(app: Application) : AndroidViewModel(app) {

    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val failure: MutableLiveData<Failure> by lazy {
        MutableLiveData<Failure>()
    }

    private val userRepository: UserRepository by lazy {
        UserRepository(
            NetworkHandler(app.applicationContext)
        )
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = userRepository.createUserWithEmailAndPassword(email, password)
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleUser)
            }
        }

    }

    private fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

    private fun handleUser(user: User?) {
        this.user.value = user
    }
}