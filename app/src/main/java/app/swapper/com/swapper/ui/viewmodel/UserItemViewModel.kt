package app.swapper.com.swapper.ui.viewmodel

import android.util.Log
import app.swapper.com.swapper.adapter.UserItemsHorizontalAdapter
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.dto.UserData
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.utils.SingleLiveEvent
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemViewModel(private val service: ApiService?, user: User?) : RecyclerViewViewModel<UserItemsHorizontalAdapter>(service, user) {

    private var adapter: UserItemsHorizontalAdapter = UserItemsHorizontalAdapter()

    val showDialog = SingleLiveEvent<Boolean>()

    override fun getAdapter(): UserItemsHorizontalAdapter {
        return adapter
    }

    fun sendItemExchangeRequest(itemId: Long) {
        if (itemId == -1L) return

        val selectedIds = adapter.getSelectedItemsIds()
        if (selectedIds.isNotEmpty()) {
            val call = service?.markItem(itemId, selectedIds)
            call?.enqueue(object : Callback<Item?> {
                override fun onResponse(call: Call<Item?>, response: Response<Item?>?) {
                    if (response != null && response.isSuccessful) {
                        adapter.resetAllSelectableStates()
                        showDialog.value = true
                    }
                }

                override fun onFailure(call: Call<Item?>, t: Throwable?) {
                    adapter.resetAllSelectableStates()
                }
            })
        }
    }
}