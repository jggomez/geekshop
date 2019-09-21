package co.devhack.tiendageek.data.repositories

import co.devhack.tiendageek.data.entities.Product
import co.devhack.tiendageek.presentation.repositories.IProductRepository
import co.devhack.tiendageek.util.Either
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.NetworkHandler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductRepository(
    private val networkHandler: NetworkHandler
) : IProductRepository {


    override suspend fun insertProduct(product: Product): Either<Failure, String> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    FirebaseFirestore
                        .getInstance()
                        .collection("products")
                        .add(product)
                        .addOnSuccessListener {
                            continuation.resume(
                                Either.Right(
                                    it.id
                                )
                            )
                        }
                        .addOnFailureListener {
                            continuation.resume(
                                Either.Left(
                                    Failure.ServerError(it)
                                )
                            )
                        }

                }

            }
            false, null -> Either.Left(Failure.NetworkConnection)
        }
    }

    override suspend fun getAll(): Either<Failure, List<Product>> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    FirebaseFirestore
                        .getInstance()
                        .collection("products")
                        .get()
                        .addOnSuccessListener {
                            continuation.resume(
                                Either.Right(
                                    it.toObjects(Product::class.java)
                                )
                            )
                        }
                        .addOnFailureListener {
                            continuation.resume(
                                Either.Left(
                                    Failure.ServerError(it)
                                )
                            )
                        }

                }

            }
            false, null -> Either.Left(Failure.NetworkConnection)
        }
    }

    override suspend fun getLastProducts(): Either<Failure, Query> {
        return when (networkHandler.isConnected) {
            true -> {
                suspendCoroutine { continuation ->
                    val task = FirebaseFirestore
                        .getInstance()
                        .collection("products")
                        .orderBy("date")
                        .whereEqualTo("active", true)

                    continuation.resume(
                        Either.Right(task)
                    )
                }

            }
            false, null -> Either.Left(Failure.NetworkConnection)
        }
    }
}