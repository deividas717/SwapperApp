package app.swapper.com.swapper.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.databinding.BindingAdapter
import android.widget.ImageView
import app.swapper.com.swapper.Constants
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.ui.viewmodel.RecyclerViewViewModel
import com.bumptech.glide.Glide
import java.io.File

/**
 * Created by Deividas on 2018-05-01.
 */
object ViewModelBindings {

    @JvmStatic
    @BindingAdapter("recyclerViewViewModel")
    fun setRecyclerViewViewModel(recyclerView: RecyclerView, viewModel: RecyclerViewViewModel) {
        viewModel.setupRecyclerView(recyclerView)
    }

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, item: Item) {
        item.images?.let {
            if (it.isNotEmpty()) {
                val url = Constants.serverAddress + "api/image" + File.separator + it[0]
                GlideLoader.loadFromApi(view.context, view, url)
            } else {

            }
        }
    }
}