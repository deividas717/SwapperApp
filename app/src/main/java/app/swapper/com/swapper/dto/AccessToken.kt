package app.swapper.com.swapper.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by Deividas on 2018-04-23.
 */
class AccessToken(val accessToken: String, @SerializedName("expires_in") var expiresIn: Long, val userId: Long)