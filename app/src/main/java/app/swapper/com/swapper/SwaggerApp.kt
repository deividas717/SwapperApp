package app.swapper.com.swapper

import android.app.Application
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.storage.SharedPreferencesManager
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
    }

    fun getUser() : User? {
        return prefs.getUser()
    }
}