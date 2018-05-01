package app.swapper.com.swapper.ui.viewmodel

import app.swapper.com.swapper.adapter.UserHorizontalGalleryAdapter
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemViewModel(private val service: ApiService?, private val user: User?) : RecyclerViewViewModel() {

    private var adapter: UserHorizontalGalleryAdapter = UserHorizontalGalleryAdapter()

    override fun getAdapter(): UserHorizontalGalleryAdapter {
        return adapter
    }

    fun askServerForUserItems() {
        user?.let {
            val call = service?.getUserItems(it.email)
            call?.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response?.let {
                        if (response.isSuccessful) {
                            val userItems = response.body()
                            userItems?.let {
                                adapter.setDataList(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {

                }
            })
        }
    }

    fun sendItemExchangeRequest(itemId: Long) {
        if (itemId == -1L) return

        val selectedIds = adapter.getSelectedItemsIds()
        if (selectedIds.isNotEmpty()) {
            val call = service?.markItem(itemId, selectedIds)
            call?.enqueue(object : Callback<RequestBody> {
                override fun onResponse(call: Call<RequestBody>?, response: Response<RequestBody>?) {
                    response?.let {
                        if (it.isSuccessful) adapter.resetAllSelectableStates()
                    }
                }

                override fun onFailure(call: Call<RequestBody>?, t: Throwable?) {
                    adapter.resetAllSelectableStates()
                }
            })
        }
    }
}