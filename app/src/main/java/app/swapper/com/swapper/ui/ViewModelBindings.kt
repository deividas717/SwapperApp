package app.swapper.com.swapper.ui

import android.support.v7.widget.RecyclerView
import android.databinding.BindingAdapter
import android.location.Location
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.swapper.com.swapper.R
import app.swapper.com.swapper.adapter.ColumnItemDecoration
import app.swapper.com.swapper.adapter.UserItemsGridAdapter
import app.swapper.com.swapper.adapter.UserItemsHorizontalAdapter
import app.swapper.com.swapper.utils.Constants
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.ui.viewmodel.RecyclerViewViewModel
import com.bumptech.glide.Glide
import java.io.File
import com.bumptech.glide.request.RequestOptions
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Created by Deividas on 2018-05-01.
 */
object ViewModelBindings {

    private val location = Location("ItemLocation")

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
        Log.d("ADUISDSDSD", "${item.id} ${item.title}")
        item.images?.let {
            if (it.isNotEmpty()) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.empty)
                requestOptions.error(R.drawable.empty)
                val url = Constants.serverAddress + "api/image" + File.separator + it[0]
                Glide.with(view).setDefaultRequestOptions(requestOptions).load(url).into(view)
            } else {
                Glide.with(view).load(R.drawable.empty).into(view)
            }
        } ?: run {
            Glide.with(view).load(R.drawable.empty).into(view)
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

    @JvmStatic
    @BindingAdapter(value = ["item", "phoneLocation"], requireAll = true)
    fun calculateDistance(view: TextView, item: Item, phoneLocation: Location?) {
        location.latitude = item.lat
        location.longitude = item.lng

        val distance = phoneLocation?.distanceTo(location)

        if (distance != null) {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            val extension = if (distance < 1) "m" else "km"
            view.text = "${df.format(distance)} $extension"
            Log.d("ASDUISDSsd", "${df.format(distance)} $extension}")
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}