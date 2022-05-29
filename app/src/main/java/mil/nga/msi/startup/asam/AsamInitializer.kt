package mil.nga.msi.startup.asam

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.asam.AsamRepository
import javax.inject.Inject

class AsamInitializer: Initializer<AsamRepository> {
   @Inject
   lateinit var asamRepository: AsamRepository

   override fun create(context: Context): AsamRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      asamRepository.fetchAsams()
      asamRepository.fetchAsamsPeriodically()
      return asamRepository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}