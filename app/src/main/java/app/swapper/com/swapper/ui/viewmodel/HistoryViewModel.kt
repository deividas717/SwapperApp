package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import app.swapper.com.swapper.ItemDataSourceFactory
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.NetworkState
import app.swapper.com.swapper.adapter.ItemKeyedItemDataSource

class HistoryViewModel(apiService: ApiService?, email: String?): ViewModel() {

    var userList: LiveData<PagedList<Item>>? = null

    init {
        if (email != null) {
            val factory = ItemDataSourceFactory(apiService, email)

            val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(PAGED_LIST_ENABLE_PLACEHOLDERS)
                    .setInitialLoadSizeHint(PAGED_LIST_PAGE_SIZE)
                    .setPageSize(PAGED_LIST_PAGE_SIZE).build()

            userList = LivePagedListBuilder(factory, pagedListConfig)
                    .build()
        }
    }

    companion object {
        private const val PAGED_LIST_PAGE_SIZE = 5
        private const val PAGED_LIST_ENABLE_PLACEHOLDERS = true
    }
}