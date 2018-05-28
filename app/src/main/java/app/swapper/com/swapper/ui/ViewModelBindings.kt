package app.swapper.com.swapper.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.widget.ImageView
import app.swapper.com.swapper.adapter.AutoFitGridLayoutManager
import app.swapper.com.swapper.adapter.ColumnItemDecoration
import app.swapper.com.swapper.adapter.UserItemsGridAdapter
import app.swapper.com.swapper.adapter.UserItemsHorizontalAdapter
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
    @BindingAdapter("recyclerViewHorizontalViewModel")
    fun recyclerViewHorizontalViewModel(recyclerView: RecyclerView, viewModel: RecyclerViewViewModel<UserItemsHorizontalAdapter>) {
        viewModel.setupRecyclerView(recyclerView)
    }

    @JvmStatic
    @BindingAdapter("recyclerViewGridViewModel")
    fun recyclerViewGridViewModel(recyclerView: RecyclerView, viewModel: RecyclerViewViewModel<UserItemsGridAdapter>) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 3)
        recyclerView.addItemDecoration(ColumnItemDecoration())
        recyclerView.clipToPadding = false
        viewModel.setupRecyclerView(recyclerView)
    }

    @JvmStatic
    @BindingAdapter("otherUserItemRecyclerViewModel")
    fun otherUserItemRecyclerViewModel(recyclerView: RecyclerView, viewModel: RecyclerViewViewModel<UserItemsGridAdapter>) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 3)
        recyclerView.addItemDecoration(ColumnItemDecoration())
        recyclerView.clipToPadding = false
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
                // todo show default image
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