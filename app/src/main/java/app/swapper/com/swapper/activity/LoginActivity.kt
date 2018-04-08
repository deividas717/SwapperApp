package app.swapper.com.swapper.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import app.swapper.com.swapper.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"))
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(loginResult.accessToken) { user, _ ->
                    val userId = user.getString("id")
                    val name = user.getString("name")
                    val email = user.getString("email")
                    val photoPath = "https://graph.facebook.com/$userId/picture?type=large"

                    startMainActivity()
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
        if (AccessToken.getCurrentAccessToken() != null) {
            startActivity(Intent(this, MainActivity::class.java));
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
