package co.devhack.tiendageek.presentation.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.devhack.tiendageek.R
import co.devhack.tiendageek.data.entities.Product
import co.devhack.tiendageek.presentation.viewmodel.PoductsViewModel
import co.devhack.tiendageek.util.BaseActivity
import co.devhack.tiendageek.util.Failure
import co.devhack.tiendageek.util.notify
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : BaseActivity() {

    private lateinit var productsViewModel: PoductsViewModel
    private lateinit var dataSourceProducts: DataSource<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        initRecyclerView()

        fabNewProduct.setOnClickListener {
            startActivity(Intent(this, NewProductActivity::class.java))
        }

        productsViewModel = ViewModelProviders.of(
            this@ProductsActivity
        ).get(PoductsViewModel::class.java)

        productsViewModel.products.observe(this, Observer {
            dataSourceProducts.clear()
            dataSourceProducts.addAll(it)
            hideProgress()
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

    private fun initRecyclerView() {
        dataSourceProducts = emptyDataSourceTyped()
        rcvOthersProducts.setup {
            withLayoutManager(
                LinearLayoutManager(
                    this@ProductsActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            withDataSource(dataSourceProducts)
            withItem<Product, ItemProduct>(R.layout.item_product) {
                onBind(::ItemProduct) { _, item ->
                    txtProductName.text = item.name
                    // TODO cargar imagen
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        showProgress()
        productsViewModel.getAllProduct()
    }

}

class ItemProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
    val imvProduct: ImageView = itemView.findViewById(R.id.imvProduct)
}
