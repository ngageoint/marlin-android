package mil.nga.msi.repository.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.network.navigationalwarning.NavigationalWarningService
import javax.inject.Inject

class NavigationalWarningRemoteDataSource @Inject constructor(
   private val service: NavigationalWarningService
) {
   suspend fun fetchNavigationalWarnings(): List<NavigationalWarning> {
      val warnings = mutableListOf<NavigationalWarning>()

      val response = service.getNavigationalWarnings()
      if (response.isSuccessful) {
         response.body()?.let {
            warnings.addAll(it.warnings)
         }
      }

      return warnings
   }
}