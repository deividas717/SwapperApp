package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.util.Log
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailItemViewModel(private val apiService: ApiService?): ViewModel() {

    val photoData = MutableLiveData<List<String>>()
    val description = ObservableField<String>()
    val loadingImages = ObservableField<Boolean>(true)

    fun getDetailItemInfo(itemId: Long) {
        val call = apiService?.getDetailItemInfo(itemId)
        call?.enqueue(object : Callback<Item?> {
            override fun onResponse(call: Call<Item?>?, response: Response<Item?>?) {
                response?.let {
                    if (it.isSuccessful) {
                        val item = it.body()
                        if (item != null) {
                            photoData.value = item.images
                            description.set(item.description)
                            loadingImages.set(false)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Item?>?, t: Throwable?) {
                Log.d("ASDSDSD", t.toString());
            }
        })
    }
}