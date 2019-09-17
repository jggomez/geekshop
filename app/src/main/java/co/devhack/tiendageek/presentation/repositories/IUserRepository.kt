package co.devhack.tiendageek.presentation.repositories

import co.devhack.tiendageek.data.entities.User
import co.devhack.tiendageek.util.Either
import co.devhack.tiendageek.util.Failure
import com.google.firebase.auth.AuthCredential

interface IUserRepository {

    suspend fun authUserWithCredentials(credencial: AuthCredential):
            Either<Failure, User?>

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Either<Failure, User?>

    suspend fun sigInUserWithEmailAndPassword(
        email: String,
        password: String
    ): Either<Failure, User?>

    suspend fun getCurrentUserAuth(): Either<Failure, User?>

    suspend fun signOutUser(): Either<Failure, Boolean>

}