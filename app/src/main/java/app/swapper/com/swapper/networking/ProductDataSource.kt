package app.swapper.com.swapper.networking

import android.arch.paging.PageKeyedDataSource
import app.swapper.com.swapper.dto.Item

class ProductDataSource(private val api: ApiService): PageKeyedDataSource<Integer, Item>() {

    override fun loadInitial(params: LoadInitialParams<Integer>, callback: LoadInitialCallback<Integer, Item>) {

    }

    override fun loadAfter(params: LoadParams<Integer>, callback: LoadCallback<Integer, Item>) {

    }

    override fun loadBefore(params: LoadParams<Integer>, callback: LoadCallback<Integer, Item>) {

    }
}