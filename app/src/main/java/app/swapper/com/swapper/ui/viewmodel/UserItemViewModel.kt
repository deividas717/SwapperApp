package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.util.Log
import app.swapper.com.swapper.State
import app.swapper.com.swapper.TradeType
import app.swapper.com.swapper.adapter.UserItemsHorizontalAdapter
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.utils.SingleLiveEvent
import app.swapper.com.swapper.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemViewModel(private val service: ApiService?, private val user: User?) : RecyclerViewViewModel<UserItemsHorizontalAdapter>(service, user) {

    private var adapter: UserItemsHorizontalAdapter = UserItemsHorizontalAdapter()

    private var currentItem = 0
    var allReceivedData : MutableList<Item> = mutableListOf()
    val data = MutableLiveData<List<Item>>()
    var location: Location? = null

    val state = SingleLiveEvent<State>()
    val isExecutingRequest = SingleLiveEvent<Boolean>()

    private var allDataIndex = 0
    private var downlaodedSize = 0

    init {
        state.value = State.SEND
        isExecutingRequest.value = false
    }

    override fun getAdapter(): UserItemsHorizontalAdapter {
        return adapter
    }

    fun indexData(index: Int, cardDismissed: Boolean = true): MutableList<Item>? {
        if (index > 0 || cardDismissed) changeCurrentItemIndex(true)
        if (index == 0 && allDataIndex > 0) return null

        val sliceIndex = SLICE_SIZE - index

        val sliceList: MutableList<Item>?
        val newSliceIndex = allDataIndex + sliceIndex
        if (allReceivedData.size > newSliceIndex) {
            sliceList = allReceivedData.subList(allDataIndex, newSliceIndex)
            allDataIndex += sliceIndex
        } else {
            val test = allReceivedData.size - allDataIndex
            sliceList = allReceivedData.subList(allDataIndex, allReceivedData.size)
            allDataIndex += test
        }

        if (allReceivedData.size - allDataIndex < 5) {
            getMoreCards()
        }

        return sliceList
    }

    fun sendItemExchangeRequest(itemId: Long) {
        if (itemId == -1L || user == null) return

        sendSelectedIds(itemId, user.userId, adapter.selectedItemsId, false)
    }

    private fun sendSelectedIds(itemId: Long, userId: Long, selectedIds: List<Long>, isEdit: Boolean) {
        if (selectedIds.isNotEmpty()) {
            isExecutingRequest.value = true
            val call = service?.sendItemSelectionList(itemId, userId, selectedIds)
            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>?) {
                    if (response != null && response.isSuccessful) {
                        adapter.changeSelectedItemsBackground()
                        isExecutingRequest.value = false
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    isExecutingRequest.value = false
                    //resetAllSelectableStates()
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
                        state.value = State.EDIT
                        isExecutingRequest.value = false
                        adapter.changeSelectedItemsBackground()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    isExecutingRequest.value = false
                    // resetAllSelectableStates()
                }
            })
        }
    }

    fun resetAllSelectableStates() {
        state.value = State.SEND
        adapter.resetAllSelectableStates()
    }

    private fun getMoreCards() {
        Utils.ifNotNull(location, user) { location, user ->
            run {
                val downloadedIds = allReceivedData.map { it.id }.toList()
                val result = service?.getNearestItems(user.email, location.latitude, location.longitude, downloadedIds)
                result?.enqueue(object : Callback<List<Item>> {
                    override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                        response.let {
                            val list = response?.body()
                            list?.let {
                                Log.d("ASDUSADSD", "getMoreCards")
                                downlaodedSize += it.size
                                allReceivedData.addAll(it)
                                data.value = it
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                        Log.d("ASDUSADSD", "onFailure")
                    }
                })
            }
        }
    }

    private fun changeCurrentItemIndex(increase : Boolean) {
        if (increase) {
            if (allReceivedData.size > currentItem) {
                markCardAsAlreadySeen(allReceivedData[currentItem].id)
                currentItem = currentItem.plus(1)
            }
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

    fun getDistance(lat: Double, lng: Double) : String? {
        return location?.let {
            val itemLocation = Location("ItemLocation")
            itemLocation.latitude = lat
            itemLocation.longitude = lng
            val distance = it.distanceTo(itemLocation)
            formatNumber(distance)
        } ?: run {
            null
        }
    }

    private fun formatNumber(distance: Float): String {
        var distance = distance
        var unit = "m"
        if (distance > 1000) {
            distance /= 1000.0.toFloat()
            unit = "km"
        }
        return String.format("%4.3f%s", distance, unit)
    }

    companion object {
        const val SLICE_SIZE = 3
    }
}