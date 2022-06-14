package mil.nga.msi.repository.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import javax.inject.Inject

class NavigationalWarningLocalDataSource @Inject constructor(
   private val dao: NavigationalWarningDao
) {
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>) = dao.insert(navigationalWarnings)

   suspend fun getNavigationalWarnings() = dao.getNavigationalWarnings()
}