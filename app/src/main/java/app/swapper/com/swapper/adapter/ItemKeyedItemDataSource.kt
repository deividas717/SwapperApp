package app.swapper.com.swapper.adapter

import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.ApiService
import android.arch.paging.PageKeyedDataSource

class ItemKeyedItemDataSource(val api: ApiService?, val email: String) : PageKeyedDataSource<Int, Item>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Item>) {
        val result = api?.getUserHistoryItems(email, 0, params.requestedLoadSize)?.execute()
        if (result != null) {
            val items = result.body() as List<Item?>
            callback.onResult(items, null, params.requestedLoadSize)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        val result = api?.getUserHistoryItems(email, params.key.toLong(), params.requestedLoadSize)?.execute()
        if (result != null) {
            val items = result.body() as List<Item?>
            callback.onResult(items, params.key + 5)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {

    }
}