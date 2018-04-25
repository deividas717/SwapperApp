package app.swapper.com.swapper

import android.app.Application
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.storage.SharedPreferencesManager
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog

/**
 * Created by Deividas on 2018-04-08.
 */
class SwaggerApp : Application() {

    private lateinit var prefs: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()

        XLog.init(LogLevel.ALL)

        prefs = SharedPreferencesManager.getInstance(applicationContext)

        getAccessToken()?.let {
            GlideLoader.accessToken = it.accessToken
        }
    }

    fun getUser() : User? {
        return prefs.getUser()
    }

    fun getAccessToken() : AccessToken? {
        return prefs.getAccessToken()
    }
}