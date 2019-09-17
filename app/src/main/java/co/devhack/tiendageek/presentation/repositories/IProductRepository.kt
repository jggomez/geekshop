package co.devhack.tiendageek.presentation.repositories

import co.devhack.tiendageek.data.entities.Product
import co.devhack.tiendageek.util.Either
import co.devhack.tiendageek.util.Failure

interface IProductRepository {

    suspend fun insertProduct(product: Product): Either<Failure, String>

    suspend fun getAll(): Either<Failure, List<Product>>
}