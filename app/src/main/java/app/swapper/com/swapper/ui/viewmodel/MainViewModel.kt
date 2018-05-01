package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import app.swapper.com.swapper.utils.SingleLiveEvent
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.databinding.ObservableList


/**
 * Created by Deividas on 2018-04-30.
 */
class MainViewModel(private val service: ApiService?, private val user: User?): ViewModel() {

    val requestForChangeSend = SingleLiveEvent<Boolean>()

    var users: ObservableList<Item>? = null

    fun askServerForUserItems() {
        user?.let {
            val call = service?.getUserItems(it.email)
            call?.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response?.let {
                        if (!response.isSuccessful) return

                        val userItems = response.body()
                        userItems?.let {
                            users?.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                    Log.d("ASDASUasdasdKDSD", t?.message + "");
                }
            })
        }
    }

    fun sendItemExchangeRequest(itemId: Long, ids: List<Long>) {
        if (ids.isNotEmpty()) {
            if (itemId != -1L) {
                val call = service?.markItem(itemId, ids)
                call?.enqueue(object : Callback<RequestBody> {
                    override fun onResponse(call: Call<RequestBody>?, response: Response<RequestBody>?) {
                        response?.let {
                            requestForChangeSend.value = it.isSuccessful
                        }
                    }

                    override fun onFailure(call: Call<RequestBody>?, t: Throwable?) {
                        requestForChangeSend.value = false
                    }
                })
            }
        }
    }
}