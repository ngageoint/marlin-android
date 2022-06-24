package mil.nga.msi.location

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext val context: Context
) : LiveData<Location>() {
    companion object {
        private val LOG_NAME = LocationProvider::class.java.simpleName
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            value = result.lastLocation
        }
    }

    override fun onActive() {
        super.onActive()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onInactive() {
        super.onInactive()

        fusedLocationClient?.removeLocationUpdates(locationCallback)
        fusedLocationClient = null
    }

    fun requestLocationUpdates() {
        fusedLocationClient?.lastLocation?.addOnCompleteListener {
            value = it.result
        }

        Log.v(LOG_NAME, "request location updates")
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY // TODO need to check if they enabled course or high
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
}
