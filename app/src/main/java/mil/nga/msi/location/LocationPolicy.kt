package mil.nga.msi.location

import android.location.Location
import androidx.lifecycle.MediatorLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationPolicy @Inject constructor(val locationProvider: LocationProvider) {

    var bestLocationProvider: MediatorLiveData<Location?> = MediatorLiveData()
    var filterLocationProvider: MediatorLiveData<Location?> = MediatorLiveData()

    private var bestLocation: Location? = null
    private var filterLocation: Location? = null

    private val bestLocationObserver = { location: Location? ->
        if (location != null && isBetterLocation(location, bestLocation)) {
            bestLocation = location
            bestLocationProvider.value = location
        }
    }

    private val filterLocationObserver = { location: Location? ->
        if (location != null && updateFilterLocation(location, filterLocation)) {
            filterLocation = location
            filterLocationProvider.value = location
        }
    }

    init {
        bestLocationProvider.addSource(locationProvider, bestLocationObserver)
        filterLocationProvider.addSource(locationProvider, filterLocationObserver)
    }

    fun requestLocationUpdates() {
        locationProvider.requestLocationUpdates()
    }

    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > LOCATION_STALE_INTERVAL
        val isSignificantlyOlder = timeDelta < -LOCATION_STALE_INTERVAL
        val isNewer = timeDelta > 0
        if (isSignificantlyNewer) { // If it's been more than two minutes since the current location, use the new location because the user has likely moved
            return true
        } else if (isSignificantlyOlder) {  // If the new location is more than two minutes older, it must be worse
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > LOCATION_ACCURACY_THRESHOLD

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isSameGPSProvider(location.provider, currentBestLocation.provider)) {
            return true
        }

        return false
    }

    private fun isSameGPSProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    private fun updateFilterLocation(location: Location, currentFilterLocation: Location?): Boolean {
        if (currentFilterLocation == null) return true
        return currentFilterLocation.distanceTo(location) > FILTER_CHANGE_DISTANCE_METERS
    }

    companion object {
        private const val LOCATION_STALE_INTERVAL = 1000 * 60 * 2
        private const val LOCATION_ACCURACY_THRESHOLD = 200
        private const val FILTER_CHANGE_DISTANCE_METERS = 10 * 1000
    }
}