package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-25.
 */
class SwipeViewModel(val user: User?, private val service: ApiService?) : ViewModel() {
    private var currentItem = 0
    private var allReceivedData : MutableList<Item> = mutableListOf()
    val data = MutableLiveData<List<Item>>()
    private var location: Location? = null

    private fun getMoreCards() {
        val downloadedIds = allReceivedData.map { it.id }.toList()
        Utils.ifNotNull(location, user) { location, user ->
            run {
                val result = if (downloadedIds.isEmpty()) {
                    service?.getNearestItems(user.email, location.latitude, location.longitude)
                } else {
                    service?.getNearestItems(user.email, location.latitude, location.longitude, downloadedIds)
                }

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
            }
        }
    }

    fun askForMoreCards(count: Int) {
        if (count < 5) {
            getMoreCards()
        }
        changeCurrentItemIndex(true)
    }

    private fun changeCurrentItemIndex(increase : Boolean) {
        if (increase) {
            markCardAsAlreadySeen(allReceivedData[currentItem].id)
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


    private fun markCardAsAlreadySeen(cardId: Long?) {
        if (cardId != null) {
            val userId = user?.userId
            if (userId != null) {
                val result = service?.markItemAsAlreadySeen(cardId, userId)
                result?.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                    }
                })
            }
        }
    }
}