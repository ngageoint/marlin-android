package mil.nga.msi.startup.asam

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class AsamInitializer: Initializer<AsamRepository> {
   @Inject
   lateinit var repository: AsamRepository

   override fun create(context: Context): AsamRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchAsams()
      repository.fetchAsamsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}