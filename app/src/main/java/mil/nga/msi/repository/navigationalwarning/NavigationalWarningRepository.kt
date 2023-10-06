package mil.nga.msi.repository.navigationalwarning

import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.startup.navigationalwarning.NavigationalWarningInitializer.Companion.FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK
import java.util.Date
import javax.inject.Inject

class NavigationalWarningRepository @Inject constructor(
   workManager: WorkManager,
   private val localDataSource: NavigationalWarningLocalDataSource,
   private val remoteDataSource: NavigationalWarningRemoteDataSource,
   private val notification: MarlinNotification,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   fun getNavigationalWarningsByArea(navigationArea: NavigationArea?) = localDataSource.observeNavigationalWarningsByArea(navigationArea)

   fun getNavigationalWarningsByNavigationArea(
      hydroarc: Date,
      hydrolant: Date,
      hydropac: Date,
      navareaIV: Date,
      navareaXII: Date,
      special: Date
   )  = localDataSource.observeNavigationalWarningsByNavigationArea(hydroarc, hydrolant, hydropac, navareaIV, navareaXII, special)

   suspend fun getNavigationalWarnings() = localDataSource.getNavigationalWarnings()

   fun getNavigationalWarnings(filters: List<Filter>): List<NavigationalWarning> {
      val query = QueryBuilder(
         table = "navigational_warnings",
         filters = filters
      ).buildQuery()
      return localDataSource.getNavigationalWarnings(query)
   }

   fun getNavigationalWarnings(
      minLatitude: Double,
      minLongitude: Double,
      maxLatitude: Double,
      maxLongitude: Double
   ) = localDataSource.getNavigationalWarnings(minLatitude, minLongitude, maxLatitude, maxLongitude)

   fun observeNavigationalWarningMapItems() = localDataSource.observeNavigationalWarningMapItems()
   fun observeUnparsedNavigationalWarnings() = localDataSource.observeUnparsedNavigationalWarnings()
   fun observeNavigationalWarning(key: NavigationalWarningKey) = localDataSource.observeNavigationalWarning(key)
   suspend fun getNavigationalWarning(key: NavigationalWarningKey) = localDataSource.getNavigationalWarning(key)

   suspend fun count(filters: List<Filter>): Int {
      val query = QueryBuilder(
         table = "navigational_warnings",
         filters = filters,
      ).buildQuery(count = true)
      return localDataSource.count(query)
   }

   suspend fun fetchNavigationalWarnings(refresh: Boolean = false): List<NavigationalWarning> {
      if (refresh) {
         val remoteWarnings = remoteDataSource.fetchNavigationalWarnings()

         val fetched = userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)
         if (fetched != null) {
            val newWarnings = remoteWarnings.subtract(localDataSource.existingNavigationalWarnings(remoteWarnings.map { it.id }).toSet()).toList()
            notification.navigationWarning(newWarnings)
         }

         localDataSource.insert(remoteWarnings)

         val localSet = sortedSetOf(NavigationalWarning.numberComparator, *localDataSource.getNavigationalWarnings().toTypedArray())
         val remoteSet = sortedSetOf(NavigationalWarning.numberComparator, *remoteWarnings.toTypedArray())
         val numbersToRemove = localSet.minus(remoteSet).map { it.number }
         localDataSource.deleteNavigationalWarnings(numbersToRemove)
      }

      return localDataSource.getNavigationalWarnings()
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}