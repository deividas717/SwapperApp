package app.swapper.com.swapper.model

import android.util.Log
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.RetrofitSingleton
import app.swapper.com.swapper.presenter.UserItemsPresenter
import app.swapper.com.swapper.view.UserItemsView
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-07.
 */
class UserItemsPresenterImpl(var view : UserItemsView) : UserItemsPresenter {

    override fun askServerForUserItems(user: User?) {
        user?.let {
            val call = RetrofitSingleton.service.getUserItems(it.email)

            call.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response?.let {
                        if (!response.isSuccessful) return

                        val userItems = response.body()
                        userItems?.let {
                            view.itemsLoaded(it)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                    Log.d("ASDASUasdasdKDSD", t?.message + "");
                }
            })
        }
    }

    override fun sendItemExchangeRequest(itemId: Long, ids: List<Long>) {
        val call = RetrofitSingleton.service.markItem(itemId, ids)

        call.enqueue(object : Callback<RequestBody> {
            override fun onResponse(call: Call<RequestBody>?, response: Response<RequestBody>?) {
                response?.let {
                    if (!response.isSuccessful) return

                }
            }

            override fun onFailure(call: Call<RequestBody>?, t: Throwable?) {
                Log.d("ASDASUasafsdfsdasdKDSD", t?.message + "");
            }
        })
    }
}