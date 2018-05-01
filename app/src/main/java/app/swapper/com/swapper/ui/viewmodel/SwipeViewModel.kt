package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.util.Log
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-25.
 */
class SwipeViewModel(val user: User?, private val service: ApiService?) : ViewModel() {
    private var index: Int = 0
    private var currentItem = 0
    private var allReceivedData : MutableList<Item> = mutableListOf()
    val data = MutableLiveData<List<Item>>()
    private var location: Location? = null

    fun getMoreCards() {
        if (location == null) return

        user?.let {
            val result = service?.getNearestItems("deividas@gmail.com", 54.7, 23.5, index)
            result?.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response.let {
                        val list = response?.body()
                        list?.let {
                            data.value = it
                            allReceivedData.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {

                }
            })
        } ?: run {
            Log.d("ASDUHSDSDS", "user null")
        }
    }


    fun increaseIndex() {
        index = index.plus(5)
    }

    fun changeCurrentItemIndex(increase : Boolean) {
        if (increase) {
            //todo mark card as already seen
            //            cardsPresenter.markCardAsAlreadySeen(user, data[currentItem].id)
            currentItem = currentItem.plus(1)
        } else {
            if (currentItem > 0) {
                currentItem = currentItem.minus(1)
            }
        }
    }

    fun changeLocation(location: Location) {
        // if location is unknown we need to load items from server!
        val isNeedToLoadImages = this.location == null
        this.location = location
        if (isNeedToLoadImages) getMoreCards()
    }

    fun getActiveCardId() : Long {
        if (allReceivedData.size <= currentItem) return -1L

        return allReceivedData[currentItem].id
    }
}