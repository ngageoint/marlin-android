package mil.nga.msi.startup.modu

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class ModuInitializer: Initializer<ModuRepository> {
   @Inject
   lateinit var repository: ModuRepository

   override fun create(context: Context): ModuRepository {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      repository.fetchModus()
      repository.fetchModusPeriodically()
      return repository
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }
}