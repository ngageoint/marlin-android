package mil.nga.msi.startup.port

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class PortInitializer: Initializer<PortRepository> {
   @Inject
   lateinit var repository: PortRepository

   override fun create(context: Context): PortRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchPorts()
      repository.fetchPortsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}