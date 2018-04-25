package app.swapper.com.swapper.model

import android.util.Log
import android.webkit.MimeTypeMap
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.RetrofitSingleton
import app.swapper.com.swapper.presenter.CreationPresenter
import app.swapper.com.swapper.view.ItemCreationView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Created by Deividas on 2018-04-07.
 */
class CreationPresenterImpl(var view : ItemCreationView, val accessToken: AccessToken?) : CreationPresenter {

    override fun sendItemDataToServer(item: Item, files: List<File>) {
        val call = RetrofitSingleton.service.createNewItem(item)
        call.enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>?, response: Response<Item>?) {
                response?.let {
                    if (!response.isSuccessful) return

                    if (files.isEmpty()) {
                        view.itemCreated()
                        return
                    }

                    val createdItem = response.body()
                    createdItem?.let {
                        sendImages(createdItem.id, files)
                    }
                }
            }

            override fun onFailure(call: Call<Item>?, t: Throwable?) {
                Log.d("ASDASUKDSD", t?.message + "");
            }
        })
    }

    private fun prepareFilePart(file: File): MultipartBody.Part? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.toString())
        extension?.let {
            val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            val requestFile = RequestBody.create(
                    MediaType.parse(type),
                    file
            )
            return MultipartBody.Part.createFormData("files", file.name, requestFile)
        }
        return null
    }

    private fun sendImages(itemId: Long, files: List<File>) {
        if (itemId == -1L) return

        val parts = mutableListOf<MultipartBody.Part>()

        files.forEach {
            prepareFilePart(it)?.let { it1 -> parts.add(it1) }
        }

        val imgUpload = RetrofitSingleton.service.uploadImages(itemId, parts)
        imgUpload.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!response.isSuccessful) return

                    view.itemCreated()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.d("ASDUSADSDSD", t?.message)
            }
        })
    }
}