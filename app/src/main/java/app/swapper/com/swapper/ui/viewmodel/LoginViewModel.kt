package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Bundle
import app.swapper.com.swapper.SingleLiveEvent
import app.swapper.com.swapper.ui.observableData.LoginStatus
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.FbToken
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.storage.SharedPreferencesManager
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-23.
 */

class LoginViewModel(private val prefs: SharedPreferencesManager, private val apiService: ApiService?) : ViewModel() {

    val status = SingleLiveEvent<LoginStatus>()

    fun loginManagerSuccess(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, _ ->
            val fbToken = FbToken(loginResult.accessToken.token)
            getAccessToken(fbToken, jsonObj)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, name, email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getAccessToken(fbToken: FbToken, jsonObj: JSONObject) {
        val result = apiService?.createUser(fbToken)
        result?.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            saveData(it, jsonObj)
                        }
                    } else {
                        status.value = LoginStatus.ERROR
                        com.facebook.AccessToken.setCurrentAccessToken(null)
                    }
                }
            }

            override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                status.value = LoginStatus.ERROR
            }
        })
    }

    private fun saveData(accessToken: app.swapper.com.swapper.dto.AccessToken, jsonObj: JSONObject) {
        val userId = jsonObj.getString("id")

        val name = jsonObj.getString("name")
        val email = jsonObj.getString("email")
        val photoUrl = "https://graph.facebook.com/$userId/picture?type=large"

        val user = User(name, photoUrl, email)
        prefs.saveUser(user)
        prefs.saveAccessToken(accessToken)

        GlideLoader.accessToken = accessToken.accessToken

        status.value = LoginStatus.SUCCESS
    }

    fun isValidToOpenNewActivity() : Boolean {
        return com.facebook.AccessToken.getCurrentAccessToken() != null && prefs.getUser() != null
    }
}