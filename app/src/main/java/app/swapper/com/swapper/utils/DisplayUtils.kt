package app.swapper.com.swapper.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager

object DisplayUtils {
    fun dp2px(context: Context, dp: Int): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)

        return (dp * displayMetrics.density + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val size = Point()
        (context as Activity).windowManager.defaultDisplay.getSize(size)
        return size.x
    }
}