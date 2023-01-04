package mil.nga.msi.location

import android.location.Location
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocationFilterService @Inject constructor(
   val filterRepository: FilterRepository
): Observer<Location> {

   private var location: Location? = null

   init {
      filterRepository.filters.asLiveData().observeForever { filters ->
         updateLocationFilter(location, filters)
      }
   }

   override fun onChanged(location: Location?) {
      val filters = runBlocking { filterRepository.filters.first() }
      updateLocationFilter(location, filters)
   }

   private fun updateLocationFilter(location: Location?, filters: Map<DataSource, List<Filter>>) {
      if (location == null) return
      this.location = location

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