package mil.nga.msi.repository.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import javax.inject.Inject

class NavigationalWarningLocalDataSource @Inject constructor(
   private val dao: NavigationalWarningDao
) {
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>) = dao.insert(navigationalWarnings)

   fun observeNavigationalWarningsGroupByArea() = dao.getNavigationalWarningsGroupByArea()
   fun observeNavigationalWarningsByArea(navigationArea: NavigationArea?) = dao.getNavigationalWarningsByArea(navigationArea)
   fun observeNavigationalWarning(key: NavigationalWarningKey) = dao.observeNavigationalWarning(key.number, key.year, key.navigationArea)

   suspend fun getNavigationalWarning(key: NavigationalWarningKey) = dao.getNavigationalWarning(key.number, key.year, key.navigationArea)
   suspend fun getNavigationalWarnings() = dao.getNavigationalWarnings()
   suspend fun deleteNavigationalWarnings(numbers: List<Int>) = dao.deleteNavigationalWarnings(numbers)
}