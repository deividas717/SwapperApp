package app.swapper.com.swapper.ui.viewmodel

import app.swapper.com.swapper.State
import app.swapper.com.swapper.adapter.UserItemsHorizontalAdapter
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.utils.SingleLiveEvent
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemViewModel(private val service: ApiService?, private val user: User?) : RecyclerViewViewModel<UserItemsHorizontalAdapter>(service, user) {

    private var adapter: UserItemsHorizontalAdapter = UserItemsHorizontalAdapter()

    val state = SingleLiveEvent<State>()
    val isExecutingRequest = SingleLiveEvent<Boolean>()

    init {
        state.value = State.SEND
        isExecutingRequest.value = false
    }

    override fun getAdapter(): UserItemsHorizontalAdapter {
        return adapter
    }

    fun sendItemExchangeRequest(itemId: Long) {
        if (itemId == -1L || user == null) return

        when (adapter.state) {
            State.SEND -> {
                sendSelectedIds(itemId, user.userId, adapter.selectedItemsId, false)
            }
            State.DELETE -> {
                deleteSelectedItemIds(itemId, user.userId, adapter.selectedItemsId)
            }
            State.EDIT -> {
                sendSelectedIds(itemId, user.userId, adapter.selectedItemsId, true)
            }
        }
    }

    private fun sendSelectedIds(itemId: Long, userId: Long, selectedIds: List<Long>, isEdit: Boolean) {
        if (selectedIds.isNotEmpty()) {
            isExecutingRequest.value = true
            val call = service?.sendItemSelectionList(itemId, userId, selectedIds)
            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>?) {
                    if (response != null && response.isSuccessful) {
                        if (isEdit) {
                            adapter.state = State.EDIT
                            state.value = State.EDIT
                        } else {
                            adapter.state = State.DELETE
                            state.value = State.DELETE
                        }
                        isExecutingRequest.value = false
                        adapter.changeSelectedItemsBackground()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    isExecutingRequest.value = false
                    resetAllSelectableStates()
                }
            })
        }
    }

    private fun deleteSelectedItemIds(itemId: Long, userId: Long, selectedIds: List<Long>) {
        if (selectedIds.isNotEmpty()) {
            isExecutingRequest.value = true
            val call = service?.deleteItemSelectedIds(itemId, userId, selectedIds)
            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>?) {
                    if (response != null && response.isSuccessful) {
                        adapter.state = State.EDIT
                        state.value = State.EDIT
                        isExecutingRequest.value = false
                        adapter.changeSelectedItemsBackground()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    isExecutingRequest.value = false
                    resetAllSelectableStates()
                }
            })
        }
    }

    fun resetAllSelectableStates() {
        adapter.state = State.SEND
        state.value = State.SEND
        adapter.resetAllSelectableStates()
    }
}