package mil.nga.msi.location

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext val context: Context
) : LiveData<Location>() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            value = result.lastLocation
        }
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onInactive() {
        super.onInactive()

        fusedLocationClient?.removeLocationUpdates(locationCallback)
        fusedLocationClient = null
    }

    fun requestLocationUpdates() {
        try {
            fusedLocationClient?.lastLocation?.addOnCompleteListener {
                value = it.result
            }

            Log.v(LOG_NAME, "request location updates")
            val locationRequest = LocationRequest.Builder(5000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (ignore: SecurityException) {}
    }

    companion object {
        private val LOG_NAME = LocationProvider::class.java.simpleName
    }
}
