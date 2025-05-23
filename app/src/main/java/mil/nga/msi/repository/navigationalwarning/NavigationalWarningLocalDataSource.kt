package mil.nga.msi.repository.navigationalwarning

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import mil.nga.sf.GeometryEnvelope
import java.util.Date
import javax.inject.Inject

class NavigationalWarningLocalDataSource @Inject constructor(
   private val dao: NavigationalWarningDao
) {
   init {
      val e1: GeometryEnvelope? = null
      val e2: GeometryEnvelope? = null

      e1?.contains(e2)
      e1?.intersects(e2)
   }

   suspend fun insert(navigationalWarnings: List<NavigationalWarning>) = dao.insert(navigationalWarnings)

   fun isEmpty() = dao.count() == 0
   suspend fun count(query: SimpleSQLiteQuery) = dao.count(query)

   suspend fun existingNavigationalWarnings(ids: List<String>) = dao.getNavigationalWarnings(ids)

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
   fun getNavigationalWarnings(query: SimpleSQLiteQuery) = dao.getNavigationalWarnings(query)

   fun getNavigationalWarnings(
      minLatitude: Double,
      minLongitude: Double,
      maxLatitude: Double,
      maxLongitude: Double
   ) = dao.getNavigationalWarnings(minLatitude, minLongitude, maxLatitude, maxLongitude)
   suspend fun deleteNavigationalWarnings(numbers: List<Int>) = dao.deleteNavigationalWarnings(numbers)
}