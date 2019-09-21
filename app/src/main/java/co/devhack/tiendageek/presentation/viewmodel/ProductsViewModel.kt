package co.devhack.tiendageek.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.devhack.tiendageek.data.entities.Product
import co.devhack.tiendageek.data.repositories.ProductRepository
import co.devhack.tiendageek.presentation.repositories.IProductRepository
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.NetworkHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsViewModel(app: Application) : AndroidViewModel(app) {

    val products: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>()
    }

    val lastProducts: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>()
    }

    val productId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val failure: MutableLiveData<Failure> by lazy {
        MutableLiveData<Failure>()
    }

    private val productRepository: IProductRepository by lazy {
        ProductRepository(
            NetworkHandler(app.applicationContext)
        )
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = productRepository.insertProduct(
                product
            )
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleInsertProduct)
            }
        }
    }

    fun getAllProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            val resp = productRepository.getAll()
            viewModelScope.launch {
                resp.either(::handleFailure, ::handleProducts)
            }
        }
    }

    fun getLastProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val query = productRepository.getLastProducts()
            viewModelScope.launch {
                query.either(::handleFailure) { query ->

                    query.addSnapshotListener { querySnapshot, exception ->
                        exception?.let {
                            handleFailure(
                                Failure.ServerError(Exception(it.message))
                            )
                        }

                        if (querySnapshot?.isEmpty == false) {
                            val lstProducts = querySnapshot.toObjects(Product::class.java)
                            lastProducts.value = lstProducts
                        }
                    }
                }
            }
        }
    }

    private fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

    private fun handleProducts(lstProducts: List<Product>) {
        this.products.value = lstProducts
    }

    private fun handleInsertProduct(productId: String) {
        this.productId.value = productId
    }

}