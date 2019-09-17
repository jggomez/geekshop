package co.devhack.tiendageek.data.repositories

import co.devhack.tiendageek.data.entities.User
import co.devhack.tiendageek.presentation.repositories.IUserRepository
import co.devhack.tiendageek.util.Either
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.NetworkHandler
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository(
    private val networkHandler: NetworkHandler
) : IUserRepository {

    override suspend fun getCurrentUserAuth(): Either<Failure, User?> {
        return when (networkHandler.isConnected) {
            true -> {
                val user = FirebaseAuth.getInstance().currentUser
                Either.Right(user?.let {
                    mapToUserEntity(it)
                })
            }
            false, null -> Either.Left(Failure.NetworkConnection)
        }

    }


    override suspend fun authUserWithCredentials(credencial: AuthCredential): Either<Failure, User?> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    FirebaseAuth.getInstance()
                        .signInWithCredential(credencial)
                        .addOnSuccessListener {
                            continuation.resume(
                                Either
                                    .Right(it.user?.let { firebaseUser ->
                                        mapToUserEntity(firebaseUser)
                                    })
                            )

                        }
                        .addOnFailureListener {
                            continuation.resume(
                                Either.Left(Failure.ServerError(it))
                            )
                        }
                }
            }
            false, null -> {
                Either.Left(Failure.NetworkConnection)
            }
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Either<Failure, User?> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnSuccessListener { authResult ->
                        authResult.user?.sendEmailVerification()
                        FirebaseAuth.getInstance().signOut()
                        continuation.resume(
                            Either.Right(
                                authResult.user?.let { firebaseUser ->
                                    mapToUserEntity(firebaseUser)
                                }
                            )
                        )
                    }.addOnFailureListener { ex ->
                        continuation.resume(
                            Either.Left(Failure.ServerError(ex))
                        )
                    }
                }
            }
            false, null -> {
                Either.Left(Failure.NetworkConnection)
            }
        }
    }

    override suspend fun sigInUserWithEmailAndPassword(
        email: String,
        password: String
    ): Either<Failure, User?> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        email,
                        password
                    ).addOnSuccessListener { authResult ->
                        if (authResult.user?.isEmailVerified == true) {
                            continuation.resume(
                                Either.Right(
                                    authResult.user?.let { firebaseUser ->
                                        mapToUserEntity(firebaseUser)
                                    }
                                )
                            )
                        } else {
                            continuation.resume(
                                Either.Left(
                                    Failure.ServerError(
                                        java.lang.Exception("Email is not Verified")
                                    )
                                )
                            )
                        }

                    }.addOnFailureListener { ex ->
                        continuation.resume(
                            Either.Left(Failure.ServerError(ex))
                        )
                    }
                }
            }
            false, null -> {
                Either.Left(Failure.NetworkConnection)
            }
        }
    }

    override suspend fun signOutUser(): Either<Failure, Boolean> {
        return when (networkHandler.isConnected) {
            true -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                    Either.Right(true)
                } catch (e: Exception) {
                    Either.Left(Failure.ServerError(e))
                }
            }
            false, null -> Either.Left(Failure.NetworkConnection)
        }
    }

    private fun mapToUserEntity(firebaseUser: FirebaseUser) =
        User(
            firebaseUser.uid,
            firebaseUser.displayName,
            firebaseUser.email,
            firebaseUser.photoUrl
        )
}