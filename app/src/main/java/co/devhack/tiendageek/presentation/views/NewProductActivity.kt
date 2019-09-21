package co.devhack.tiendageek.presentation.views

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.devhack.tiendageek.R
import co.devhack.tiendageek.data.entities.Product
import co.devhack.tiendageek.presentation.viewmodel.ProductsViewModel
import co.devhack.tiendageek.util.BaseActivity
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.notify
import kotlinx.android.synthetic.main.activity_new_product.*
import java.text.SimpleDateFormat
import java.util.*

class NewProductActivity : BaseActivity() {

    private lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        productsViewModel = ViewModelProviders.of(
            this@NewProductActivity
        ).get(ProductsViewModel::class.java)

        btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        productsViewModel.productId.observe(this, Observer {
            hideProgress()
            finish()
        })

        productsViewModel.failure.observe(this, Observer { failure ->
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

    private fun saveProduct() {
        val nameProduct = txtProductName.text.toString()
        val quantityProduct = txtProductQuantity.text.toString()
        val priceProduct = txtPrice.text.toString()
        val descriptionProduct = txtDescription.text.toString()

        showProgress()

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        productsViewModel.insertProduct(
            Product(
                name = nameProduct,
                quantity = quantityProduct.toInt(),
                price = priceProduct.toDouble(),
                description = descriptionProduct,
                date = format.format(Date()),
                active = true
            )
        )
    }


}
