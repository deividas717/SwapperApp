package app.swapper.com.swapper.ui

import android.support.v7.widget.RecyclerView
import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.widget.ImageView
import app.swapper.com.swapper.utils.Constants
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.ui.viewmodel.RecyclerViewViewModel
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

    @JvmStatic
    @BindingAdapter("fabVisibilityAnim")
    fun fabVisibilityAnim(fab: FloatingActionButton, selectedItems: List<Int>) {
        if (selectedItems.isEmpty()) {
            fab.hide()
        } else {
            fab.show()
        }
    }
}