package app.swapper.com.swapper

import android.content.Intent
import app.swapper.com.swapper.di.DaggerAppComponent
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
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
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


/**
 * Created by Deividas on 2018-04-08.
 */
class SwapperApp : DaggerApplication() {

    private lateinit var prefs: SharedPreferencesManager
    private var service: ApiService? = null
    private var isHeaderEmpty = true

    override fun applicationInjector(): AndroidInjector<out SwapperApp> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        XLog.init(LogLevel.ALL)

        AppVisibilityDetector.init(this@SwapperApp, object : AppVisibilityCallback {
            override fun onAppGotoForeground() {
                //app is from background to foreground
            }

            override fun onAppGotoBackground() {
                stopService(Intent(applicationContext, LocationService::class.java))
            }
        })
    }
}