package app.swapper.com.swapper.storage

import android.content.Context
import android.content.SharedPreferences
import app.swapper.com.swapper.SingletonHolder
import app.swapper.com.swapper.dto.User
import com.elvishew.xlog.XLog

/**
 * Created by Deividas on 2018-04-08.
 */
class SharedPreferencesManager private constructor(context: Context) {

    private val name = "name"
    private val picture = "picture"
    private val email = "email"

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
        val name = prefs.getString(name, null)
        val img = prefs.getString(picture, null)
        val email = prefs.getString(email, null)

        XLog.st(5).d("User $name $img $email")

        if (!isPropValid(name) || !isPropValid(img) || !isPropValid(email)) return null
        return User(name, img, email)
    }

    fun clearUser() {
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