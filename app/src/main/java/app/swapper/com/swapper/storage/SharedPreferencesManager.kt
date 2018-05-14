package app.swapper.com.swapper.storage

import android.content.Context
import android.content.SharedPreferences
import app.swapper.com.swapper.SingletonHolder
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.User
import com.elvishew.xlog.XLog

/**
 * Created by Deividas on 2018-04-08.
 */
class SharedPreferencesManager private constructor(context: Context) {

    private val name = "name"
    private val picture = "picture"
    private val email = "email"
    private val userId = "userId"

    private val accessToken = "accessToken"
    private val expiresIn = "expiresIn"

    private var prefs : SharedPreferences = context.getSharedPreferences("USER_INFO_PREFS", Context.MODE_PRIVATE)

    companion object : SingletonHolder<SharedPreferencesManager, Context>(::SharedPreferencesManager)

    fun saveUser(user: User) {
        val editor = prefs.edit()
        editor.putString(name, user.name)
                .putString(picture, user.img)
                .putString(email, user.email)
                .apply()
    }

    fun getUser() : User? {
        val userId = prefs.getLong(userId, -1L)
        val name = prefs.getString(name, null)
        val img = prefs.getString(picture, null)
        val email = prefs.getString(email, null)

        if (userId != -1L && !isPropValid(name) || !isPropValid(img) || !isPropValid(email)) return null
        return User(userId, name, img, email)
    }

    fun saveAccessToken(accessTokenObj: AccessToken) {
        val editor = prefs.edit()
        val expiresInVal = accessTokenObj.expiresIn
        editor.putString(accessToken, "Bearer ${accessTokenObj.accessToken}")
                .putLong(expiresIn, expiresInVal)
                .apply()
    }

    fun getAccessToken(): AccessToken? {
        val userId = prefs.getLong(userId, -1L)
        val accessToken = prefs.getString(accessToken, "")
        val expiresIn = prefs.getLong(expiresIn, -1)

        return AccessToken(accessToken, expiresIn, userId)
    }

    fun clearAllData() {
        val editor = prefs.edit()
        editor.remove(name)
                .remove(picture)
                .remove(email)
                .apply()
    }

    private fun isPropValid(value: String?) : Boolean {
        value?.let {
            if (it.isNotEmpty())  return true
            return false
        } ?: return false
    }
}