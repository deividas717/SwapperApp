package app.swapper.com.swapper.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Looper
import app.swapper.com.swapper.events.LocationChangeEvent
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import android.os.IBinder
import android.util.Log
import app.swapper.com.swapper.LocationData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException

class LocationService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        locationRequest = LocationRequest.create()
        locationRequest.interval = 60000
        locationRequest.fastestInterval = 50000
        locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    LocationData.location = it
                    EventBus.getDefault().post(LocationChangeEvent(it))
                    Log.d("ASDGUIASDS", "lastLocation");
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                LocationData.location = it
                EventBus.getDefault().post(LocationChangeEvent(it))
                Log.d("ASDGUIASDS", "location");
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
            } catch (exception : ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = exception as ResolvableApiException
                            EventBus.getDefault().post(resolvable)
                        } catch (e : IntentSender.SendIntentException) {
                        } catch (e : ClassCastException) {
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("ASDGUIASDS", "onDestroy");

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
