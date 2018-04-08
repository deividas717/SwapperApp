package app.swapper.com.swapper.networking

import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Deividas on 2018-04-07.
 */
interface ApiService {

    @POST("createUser")
    fun createUser(@Body user: User) : Call<User>

    @GET("nearestItems/{email}/{lat}/{lng}/{beginAt}")
    fun getNearestItems(@Path("email") email : String,
                        @Path("lat") lat : Double,
                        @Path("lng") lng : Double,
                        @Path("beginAt") beginAt : Int): Call<List<Item>>

    @POST("createNewItem")
    fun createNewItem(@Body item : Item) : Call<Item>

    @Multipart
    @POST("uploadImages")
    fun uploadImages(@Part("itemId") itemId : Long,
                     @Part files : List<MultipartBody.Part>): Call<ResponseBody>

    @GET("userItems/{email}")
    fun getUserItems(@Path("email") email : String): Call<List<Item>>

    @POST("markItem/{itemId}")
    fun markItem(@Path("itemId") itemId : Long,
                 @Body numbers : List<Long>) : Call<RequestBody>
}