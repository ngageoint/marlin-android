package mil.nga.msi.startup.light

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class LightInitializer: Initializer<LightRepository> {
   @Inject
   lateinit var repository: LightRepository

   override fun create(context: Context): LightRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchLights()
      repository.fetchLightsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}