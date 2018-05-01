package app.swapper.com.swapper.ui.activity

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.util.Log
import app.swapper.com.swapper.events.LocationChangeEvent
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionRequestResult
import com.markodevcic.peko.rationale.AlertDialogPermissionRationale
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CHECK_SETTINGS = 4
    }

    protected fun requestPermission(vararg permissions: String) {
        launch(UI) {
            val rationale = AlertDialogPermissionRationale(applicationContext) {
                this.setTitle("Need permissions")
                this.setMessage("Please give permissions to use this feature")
            }
            Peko
            val result = Peko.requestPermissionsAsync(this@BaseActivity, *permissions, rationale = rationale).await()
            setResults(result)
        }
    }

    abstract fun onPermissionGranted(grantedPermissions : Collection<String>)
    abstract fun onPermissionDenied(deniedPermissions : Collection<String>)

    private fun setResults(result: PermissionRequestResult) {
        val (grantedPermissions, deniedPermissions) = result

        onPermissionGranted(grantedPermissions)
        onPermissionDenied(deniedPermissions)
    }

    override fun onResume() {
        super.onResume()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onCheckPermissionEvent(obj: ResolvableApiException) {
        obj.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(applicationContext, "GPS enabled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "GPS is not enabled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
