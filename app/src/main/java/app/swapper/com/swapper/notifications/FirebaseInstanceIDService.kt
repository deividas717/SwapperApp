package app.swapper.com.swapper.notifications

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId

class FirebaseInstanceIDService: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken = FirebaseInstanceId.getInstance()
        Log.d("SADGSADASD", "Refreshed token: " + refreshedToken);
    }
}