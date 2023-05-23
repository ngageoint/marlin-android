package mil.nga.msi.repository.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import java.util.*
import javax.inject.Inject

class NavigationalWarningLocalDataSource @Inject constructor(
   private val dao: NavigationalWarningDao
) {
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>) = dao.insert(navigationalWarnings)

   fun isEmpty() = dao.count() == 0
   suspend fun existingNavigationalWarnings(ids: List<String>) = dao.getNavigationalWarnings(ids)

   fun observeNavigationalWarnings() = dao.observeNavigationalWarnings()
   fun observeNavigationalWarningMapItems() = dao.observeNavigationalWarningMapItems()
   fun observeUnparsedNavigationalWarnings() = dao.observeUnparsedNavigationalWarnings()
   fun observeNavigationalWarningsByArea(navigationArea: NavigationArea?) = dao.getNavigationalWarningsByArea(navigationArea)
   fun observeNavigationalWarning(key: NavigationalWarningKey) = dao.observeNavigationalWarning(key.number, key.year, key.navigationArea)
   fun observeNavigationalWarningsByNavigationArea(
      hydroarc: Date,
      hydrolant: Date,
      hydropac: Date,
      navareaIV: Date,
      navareaXII: Date,
      special: Date
   )  = dao.getNavigationalWarningsByNavigationArea(hydroarc, hydrolant, hydropac, navareaIV, navareaXII, special)

   suspend fun getNavigationalWarning(key: NavigationalWarningKey) = dao.getNavigationalWarning(key.number, key.year, key.navigationArea)
   suspend fun getNavigationalWarnings() = dao.getNavigationalWarnings()
   suspend fun deleteNavigationalWarnings(numbers: List<Int>) = dao.deleteNavigationalWarnings(numbers)
}