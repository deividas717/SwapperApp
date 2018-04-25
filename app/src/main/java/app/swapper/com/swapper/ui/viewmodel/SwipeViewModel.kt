package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.RetrofitSingleton
import com.elvishew.xlog.XLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-25.
 */
class SwipeViewModel(val user: User?) : ViewModel() {
    private var index: Int = 0
    private var currentItem = 0

    private var allReceivedData : MutableList<Item> = mutableListOf()

    val data = MutableLiveData<List<Item>>()

    fun getMoreCards() {
        XLog.st(5).d("ASDADSDSD")
        user?.let {
            val result = RetrofitSingleton.service.getNearestItems("deividas@gmail.com", 54.7, 23.5, index)
            result.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response.let {
                        val list = response?.body()
                        list?.let {
                            data.value = it
                            //data.value?.failure = false
                            allReceivedData.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                    Log.d("ASDUHSDSDS", t?.message)
                    //data.value?.failure = true
                }
            })
        } ?: run {
            Log.d("ASDUHSDSDS", "user null")
            //data.value?.failure = false
        }
    }

    fun increaseIndex() {
        index = index.plus(5)
    }

    fun changeCurrentItemIndex(increase : Boolean) {
        if (increase) {
            //todo mark card as already seen
            //            cardsPresenter.markCardAsAlreadySeen(user, dataList[currentItem].id)
            currentItem = currentItem.plus(1)
        } else {
            if (currentItem > 0) {
                currentItem = currentItem.minus(1)
            }
        }
    }

    fun getActiveCardId() : Long {
        if (allReceivedData.size <= currentItem) return -1L

        return allReceivedData[currentItem].id
    }
}