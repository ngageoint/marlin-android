package mil.nga.msi.startup.navigationalwarning

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class NavigationalWarningInitializer: Initializer<NavigationalWarningRepository> {
   @Inject
   lateinit var repository: NavigationalWarningRepository

   override fun create(context: Context): NavigationalWarningRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchNavigationalWarnings()
      repository.fetchNavigationalWarningsPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}