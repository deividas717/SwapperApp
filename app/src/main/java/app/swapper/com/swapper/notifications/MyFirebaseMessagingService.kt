package app.swapper.com.swapper.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import app.swapper.com.swapper.R
import android.content.Intent
import app.swapper.com.swapper.ui.activity.DetailItemActivity


class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage?) {
        super.onMessageReceived(msg)

        if (msg != null) {
            val notificationId = Random().nextInt(60000)

            val detailIntent = Intent(applicationContext, DetailItemActivity::class.java)
            val buttonIntent = Intent(applicationContext, AutoDismissReceiver::class.java)
            buttonIntent.putExtra("notificationId", notificationId)

            val dismissPendingIntent = PendingIntent.getBroadcast(this, notificationId, buttonIntent, 0)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, "4")
                    .setSmallIcon(android.R.drawable.arrow_up_float)  //a resource for your custom small icon
                    .setContentTitle(msg.data["title"]) //the "title" value you sent in your notification
                    .setContentText(msg.data["message"]) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .addAction(android.R.drawable.arrow_up_float, "More details",
                            PendingIntent.getActivity(this,
                                    2,
                                    detailIntent,
                                    PendingIntent.FLAG_ONE_SHOT
                            )
                    ).addAction(android.R.drawable.arrow_up_float, "Close", dismissPendingIntent)
                    .setSound(defaultSoundUri)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}