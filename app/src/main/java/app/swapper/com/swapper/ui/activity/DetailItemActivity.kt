package app.swapper.com.swapper.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.databinding.ActivityDetailItemBinding
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.ui.factory.DetailItemViewModelFactory
import app.swapper.com.swapper.ui.viewmodel.DetailItemViewModel
import app.swapper.com.swapper.utils.Constants
import kotlinx.android.synthetic.main.activity_detail_item.*
import kotlinx.android.synthetic.main.content_detail_item.*
import com.synnapps.carouselview.ImageListener
import java.io.File


class DetailItemActivity : BaseActivity() {
    private lateinit var viewModel: DetailItemViewModel
    private val data = mutableListOf<String>()

    private var imageListener: ImageListener = ImageListener { position, imageView ->
        GlideLoader.loadFromApi(this, imageView, Constants.serverAddress + "api/image" + File.separator + data[position])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val swaggerApp = application as SwaggerApp
        val apiService = swaggerApp.getRetrofit()

        viewModel = ViewModelProviders.of(this, DetailItemViewModelFactory(apiService)).get(DetailItemViewModel::class.java)
        val binding: ActivityDetailItemBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_item)
        binding.itemData = viewModel

        viewModel.photoData.observe(this, android.arch.lifecycle.Observer { handlePhotoData(it) })

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemId = intent.getLongExtra(ITEM_ID, -1L)
        if (itemId != -1L) {
            viewModel.getDetailItemInfo(itemId)
        }

        fab.setOnClickListener({
            startActivity(UserItemsActivity.getIntent(applicationContext))
        })

        carouselView.pageCount = 0
        carouselView.setImageListener(imageListener)
    }

    private fun handlePhotoData(data: List<String>?) {
        carouselView.setImageListener(null)
        if (data != null) {
            this.data.addAll(data)
            carouselView.setImageListener(imageListener)
            carouselView.pageCount = data.size
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onPermissionGranted(grantedPermissions: Collection<String>) {

    }

    override fun onPermissionDenied(deniedPermissions: Collection<String>) {

    }

    companion object {
        const val ITEM_ID = "itemId"

        fun createNewIntent(context: Context?, itemId: Long): Intent {
            return Intent(context, DetailItemActivity::class.java).putExtra(ITEM_ID, itemId)
        }
    }
}
