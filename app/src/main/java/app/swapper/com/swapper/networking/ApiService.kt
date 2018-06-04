package app.swapper.com.swapper.networking

import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.FbToken
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Deividas on 2018-04-07.
 */
interface ApiService {

    @POST("auth/login")
    fun loginOrRegister(@Body fbToken: FbToken) : Call<AccessToken>

    @GET("api/nearestItems/{email}/{lat}/{lng}/{downloadedIds}")
    fun getNearestItems(@Path("email") email : String,
                        @Path("lat") lat : Double,
                        @Path("lng") lng : Double,
                        @Query("downloadedIds") downloadedIds : List<Long>): Call<List<Item>>

    @GET("api/nearestItems/{email}/{lat}/{lng}")
    fun getNearestItems(@Path("email") email : String,
                        @Path("lat") lat : Double,
                        @Path("lng") lng : Double): Call<List<Item>>

    @POST("api/createNewItem")
    fun createNewItem(@Body item : Item) : Call<Item>

    @Multipart
    @POST("api/uploadImages")
    fun uploadImages(@Part("itemId") itemId : Long,
                     @Part files : List<MultipartBody.Part>): Call<ResponseBody>

    @GET("api/user/{email}")
    fun getUserItems(@Path("email") email : String): Call<UserData>

    @POST("api/markItem/{itemId}/{userId}")
    fun sendItemSelectionList(@Path("itemId") itemId : Long,
                 @Path("userId") userId : Long,
                 @Body numbers : List<Long>) : Call<ResponseBody>

    @HTTP(method = "DELETE", hasBody = true, path = "api/deleteItemSelectedIds/{itemId}/{userId}")
    fun deleteItemSelectedIds(@Path("itemId") itemId : Long,
                 @Path("userId") userId : Long,
                 @Body numbers : List<Long>) : Call<ResponseBody>

    @POST("api/markItem/{itemId}/{userId}")
    fun updateItemSuggestionList(@Path("itemId") itemId : Long,
                 @Path("userId") userId : Long,
                 @Body numbers : List<Long>) : Call<ResponseBody>

    @GET("api/detailItemInfo/{itemId}")
    fun getDetailItemInfo(@Path("itemId") itemId : Long) : Call<Item?>

    @GET("api/getCandidateItems/{itemId}")
    fun getCandidateItems(@Path("itemId") itemId : Long) : Call<List<Item?>?>

    @POST("api/markItemAsAlreadySeen/{itemId}/{userId}")
    fun markItemAsAlreadySeen(@Path("itemId") itemId : Long, @Path("userId") userId : Long) : Call<ResponseBody>
}