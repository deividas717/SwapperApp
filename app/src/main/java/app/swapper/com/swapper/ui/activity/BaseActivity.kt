package app.swapper.com.swapper.ui.activity

import android.location.Location
import android.support.v7.app.AppCompatActivity
import app.swapper.com.swapper.events.LocationChangeEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

open class BaseActivity : AppCompatActivity() {

    protected var location : Location? = null

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
    fun onLocationChanged(locationObj : LocationChangeEvent) {
        location = locationObj.location
    }
}
