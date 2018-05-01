package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import app.swapper.com.swapper.adapter.UserHorizontalGalleryAdapter


/**
 * Created by Deividas on 2018-05-01.
 */
abstract class RecyclerViewViewModel: ViewModel() {

    protected abstract fun getAdapter(): UserHorizontalGalleryAdapter

    fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = getAdapter()
    }
}