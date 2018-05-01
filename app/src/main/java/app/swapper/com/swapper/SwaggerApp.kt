package app.swapper.com.swapper

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.storage.SharedPreferencesManager
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import app.swapper.com.swapper.utils.AppVisibilityDetector.AppVisibilityCallback
import app.swapper.com.swapper.service.LocationService
import app.swapper.com.swapper.utils.AppVisibilityDetector
import app.swapper.com.swapper.utils.Constants


/**
 * Created by Deividas on 2018-04-08.
 */
class SwaggerApp : Application() {

    private lateinit var prefs: SharedPreferencesManager
    private var service: ApiService? = null
    private var isHeaderEmpty = true

    override fun onCreate() {
        super.onCreate()

        XLog.init(LogLevel.ALL)

        prefs = SharedPreferencesManager.getInstance(applicationContext)

        GlideLoader.accessToken = getAccessToken()?.accessToken

        AppVisibilityDetector.init(this@SwaggerApp, object : AppVisibilityCallback {
            override fun onAppGotoForeground() {
                //app is from background to foreground
            }

            override fun onAppGotoBackground() {
                stopService(Intent(applicationContext, LocationService::class.java))
            }
        })
    }

    fun getUser() : User? {
        return prefs.getUser()
    }

    private fun getAccessToken() : AccessToken? {
        return prefs.getAccessToken()
    }

    fun getRetrofit() : ApiService? {
        val token = getAccessToken()?.accessToken
        XLog.st(5).d("ASDUIASDSD ${service == null} test ${TextUtils.isEmpty(token)}")
        if (service == null || isHeaderEmpty) {
            val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .addInterceptor { chain ->
                        isHeaderEmpty = (chain.request().header("Authorization") == null)
                        val request = chain.request().newBuilder().addHeader("Authorization", token ?: "").build()
                        chain.proceed(request)
                    }
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.serverAddress)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            service = retrofit.create(ApiService::class.java)
        }
        return service
    }
}