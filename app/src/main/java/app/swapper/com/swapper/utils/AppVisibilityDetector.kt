package app.swapper.com.swapper.utils

/**
 * Created by Deividas on 2018-04-30.
 */
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log

object AppVisibilityDetector {
    private val DEBUG = false
    private const val TAG = "AppVisibilityDetector"
    private var sAppVisibilityCallback: AppVisibilityCallback? = null
    private var sIsForeground = false
    private var sHandler: Handler? = null
    private const val MSG_GOTO_FOREGROUND = 1
    private const val MSG_GOTO_BACKGROUND = 2

    fun init(app: Application, appVisibilityCallback: AppVisibilityCallback) {
        if (!checkIsMainProcess(app)) {
            return
        }

        sAppVisibilityCallback = appVisibilityCallback
        app.registerActivityLifecycleCallbacks(AppActivityLifecycleCallbacks())

        sHandler = object : Handler(app.mainLooper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_GOTO_FOREGROUND -> {
                        if (DEBUG) {
                            Log.d(TAG, "handleMessage(MSG_GOTO_FOREGROUND)")
                        }
                        performAppGotoForeground()
                    }
                    MSG_GOTO_BACKGROUND -> {
                        if (DEBUG) {
                            Log.d(TAG, "handleMessage(MSG_GOTO_BACKGROUND)")
                        }
                        performAppGotoBackground()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun checkIsMainProcess(app: Application): Boolean {
        val activityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false

        var currProcessName: String? = null
        val currPid = android.os.Process.myPid()
        //find the process name
        for (processInfo in runningAppProcessInfoList) {
            if (null != processInfo && processInfo.pid == currPid) {
                currProcessName = processInfo.processName
            }
        }

        //is current process the main process
        return TextUtils.equals(currProcessName, app.packageName)

    }

    private fun performAppGotoForeground() {
        if (!sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = true
            sAppVisibilityCallback!!.onAppGotoForeground()
        }
    }

    private fun performAppGotoBackground() {
        if (sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = false
            sAppVisibilityCallback!!.onAppGotoBackground()
        }
    }

    interface AppVisibilityCallback {
        fun onAppGotoForeground()

        fun onAppGotoBackground()
    }

    private class AppActivityLifecycleCallbacks : ActivityLifecycleCallbacks {
        internal var activityDisplayCount = 0

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityCreated")
            }
        }

        override fun onActivityStarted(activity: Activity) {
            sHandler!!.removeMessages(MSG_GOTO_FOREGROUND)
            sHandler!!.removeMessages(MSG_GOTO_BACKGROUND)
            if (activityDisplayCount == 0) {
                sHandler!!.sendEmptyMessage(MSG_GOTO_FOREGROUND)
            }
            activityDisplayCount++

            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityStarted "
                        + " activityDisplayCount: " + activityDisplayCount)
            }
        }

        override fun onActivityResumed(activity: Activity) {
            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityResumed")
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityPaused")
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivitySaveInstanceState")
            }
        }

        override fun onActivityStopped(activity: Activity) {
            sHandler?.removeMessages(MSG_GOTO_FOREGROUND)
            sHandler?.removeMessages(MSG_GOTO_BACKGROUND)
            if (activityDisplayCount > 0) {
                activityDisplayCount--
            }

            if (activityDisplayCount == 0) {
                sHandler!!.sendEmptyMessage(MSG_GOTO_BACKGROUND)
            }

            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityStopped "
                        + " activityDisplayCount: " + activityDisplayCount)
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (DEBUG) {
                Log.d(TAG, activity.javaClass.name + " onActivityDestroyed")
            }
        }
    }
}
