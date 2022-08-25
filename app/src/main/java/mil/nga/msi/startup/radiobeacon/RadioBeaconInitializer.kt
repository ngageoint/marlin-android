package mil.nga.msi.startup.radiobeacon

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class RadioBeaconInitializer: Initializer<RadioBeaconRepository> {
   @Inject
   lateinit var repository: RadioBeaconRepository

   override fun create(context: Context): RadioBeaconRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchRadioBeacons()
      repository.fetchRadioBeaconsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}