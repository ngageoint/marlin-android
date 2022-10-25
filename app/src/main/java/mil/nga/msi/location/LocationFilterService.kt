package mil.nga.msi.location

import android.app.Service
import android.content.Intent
import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@AndroidEntryPoint
open class LocationFilterService : LifecycleService(), Observer<Location> {
   @Inject protected lateinit var locationPolicy: LocationPolicy
   @Inject protected lateinit var filterRepository: FilterRepository

   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      super.onStartCommand(intent, flags, startId)

      locationPolicy.filterLocationProvider.observe(this, this)

      return Service.START_STICKY
   }

   override fun onDestroy() {
      super.onDestroy()

      locationPolicy.filterLocationProvider.removeObserver(this)
   }

   override fun onChanged(location: Location?) {
      if (location == null) return

      val filters = runBlocking { filterRepository.filters.first() }

      filters.forEach { entry ->
         val locationFilter = entry.value.find { it.parameter.type == FilterParameterType.LOCATION && it.comparator.name == ComparatorType.NEAR_ME.name }
         val newFilters = entry.value.toMutableList()
         newFilters.remove(locationFilter)

         locationFilter?.let { filter ->
            val value = filter.value.toString()
            val values = if (value.isNotEmpty()) value.split(",") else emptyList()
            val distance = values.getOrNull(2) ?: ""

            val newFilter = Filter(
               parameter = filter.parameter,
               comparator = filter.comparator,
               value = "${location.latitude},${location.longitude},${distance}"
            )

            newFilters.add(newFilter)

            runBlocking {
               filterRepository.setFilter(entry.key, newFilters)
            }
         }
      }
   }
}