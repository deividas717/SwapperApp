package app.swapper.com.swapper.activity

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import app.swapper.com.swapper.R
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.RetrofitSingleton
import app.swapper.com.swapper.storage.SharedPreferencesManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private lateinit var prefs : SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)

        prefs = SharedPreferencesManager.getInstance(applicationContext)

        loginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"))
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, _ ->
                    createNewUser(jsonObj);
                }
                val parameters = Bundle()
                parameters.putString("fields", "id, name, email")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                AccessToken.setCurrentAccessToken(null)
            }

            override fun onError(exception: FacebookException) {
                AccessToken.setCurrentAccessToken(null)
            }
        })
    }

    override fun onStart() {
        super.onStart()

        startMainActivity()
    }

    private fun startMainActivity() {
        if (AccessToken.getCurrentAccessToken() != null && prefs.getUser() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createNewUser(jsonObj: JSONObject) {
        val userId = jsonObj.getString("id")
        val name = jsonObj.getString("name")
        val email = jsonObj.getString("email")
        val photoUrl = "https://graph.facebook.com/$userId/picture?type=large"

        val user = User(name, photoUrl, email)

        val result = RetrofitSingleton.service.createUser(user)

        result.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response.let {
                    response?.let {
                        if (it.isSuccessful) {
                            prefs.saveUser(user)

                            startMainActivity()
                        } else {
                            AccessToken.setCurrentAccessToken(null)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<User>?, t: Throwable?) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
