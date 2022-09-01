package mil.nga.msi.startup.dgpsstation

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class DgpsStationInitializer: Initializer<DgpsStationRepository> {
   @Inject
   lateinit var repository: DgpsStationRepository

   override fun create(context: Context): DgpsStationRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchDgpsStations()
      repository.fetchDgpsStationsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}