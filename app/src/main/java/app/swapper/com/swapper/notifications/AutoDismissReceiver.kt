package app.swapper.com.swapper.notifications

import android.app.NotificationManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class AutoDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notificationId", 0)
        if (notificationId != null) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        }
    }
}