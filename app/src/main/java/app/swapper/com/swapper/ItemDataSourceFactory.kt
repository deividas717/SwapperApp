package app.swapper.com.swapper

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import app.swapper.com.swapper.adapter.ItemKeyedItemDataSource
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.ApiService

class ItemDataSourceFactory(val api: ApiService?, val email: String): DataSource.Factory<Int, Item>() {
    val mutableLiveData = MutableLiveData<ItemKeyedItemDataSource>()

    override fun create(): DataSource<Int, Item> {
        val itemKeyedUserDataSource = ItemKeyedItemDataSource(api, email)
        mutableLiveData.postValue(itemKeyedUserDataSource)
        return itemKeyedUserDataSource
    }
}