package app.swapper.com.swapper

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.location.Location
import android.os.Bundle
import app.swapper.com.swapper.events.LocationChangeEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import org.greenrobot.eventbus.EventBus


class LocationService : IntentService("LocationService"),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate() {
        super.onCreate()

        googleApiClient = GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER

        googleApiClient.connect()
    }

    // permission is granted before this service is init
    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
        )
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            EventBus.getDefault().post(LocationChangeEvent(location))
        }
    }

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onDestroy() {
        super.onDestroy()

        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient.disconnect()
        }
    }
}
