package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import android.webkit.MimeTypeMap
import app.swapper.com.swapper.CompressFile
import app.swapper.com.swapper.SingleLiveEvent
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Created by Deividas on 2018-04-28.
 */
class CreateNewItemViewModel(private val apiService: ApiService?) : ViewModel() {

    private var compressedImgs : MutableList<File> = mutableListOf()
    var isItemCreated = SingleLiveEvent<Boolean>()

    fun sendItemDataToServer(item: Item) {
        val call = apiService?.createNewItem(item)
        call?.enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>?, response: Response<Item>?) {
                response?.let {
                    if (!response.isSuccessful) return

                    if (compressedImgs.isEmpty()) {
                        isItemCreated.value = true
                        return
                    }

                    val createdItem = response.body()
                    createdItem?.let {
                        sendImages(createdItem.id, compressedImgs)
                    }
                }
            }

            override fun onFailure(call: Call<Item>?, t: Throwable?) {
                Log.d("ASDASUKDSD", t?.message + "");
            }
        })
    }

    fun addToCompressedImageArray(file: File) {
        compressedImgs.add(CompressFile.saveBitmapToFile(file))
    }

    private fun sendImages(itemId: Long, files: List<File>) {
        if (itemId == -1L) return

        val parts = mutableListOf<MultipartBody.Part>()

        files.forEach {
            prepareFilePart(it)?.let { it1 -> parts.add(it1) }
        }

        val imgUpload = apiService?.uploadImages(itemId, parts)
        imgUpload?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!response.isSuccessful) return

                    isItemCreated.value = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.d("ASDUSADSDSD", t?.message)
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
}