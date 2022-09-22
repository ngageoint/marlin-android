package mil.nga.msi.repository.navigationalwarning

import androidx.lifecycle.map
import androidx.work.*
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.navigationalwarning.RefreshNavigationalWarningWorker
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NavigationalWarningRepository @Inject constructor(
   private val workManager: WorkManager,
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

   fun observeNavigationalWarning(key: NavigationalWarningKey) = localDataSource.observeNavigationalWarning(key)
   suspend fun getNavigationalWarning(key: NavigationalWarningKey) = localDataSource.getNavigationalWarning(key)

   suspend fun fetchNavigationalWarnings(refresh: Boolean = false): List<NavigationalWarning> {
      if (refresh) {
         val remoteWarnings = remoteDataSource.fetchNavigationalWarnings()

         val fetched = userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)
         if (fetched == null) {
            val newWarnings = remoteWarnings.subtract(localDataSource.existingNavigationalWarnings(remoteWarnings.map { it.compositeKey() }).toSet()).toList()
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

   fun fetchNavigationalWarnings() {
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshNavigationalWarningWorker::class.java)
         .setConstraints(
            Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .enqueueUniqueWork(
            FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchNavigationalWarningsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshNavigationalWarningWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_NAVIGATIONAL_WARNINGS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK = "FetchLatestNavigationalWarningsTask"
      private const val TAG_FETCH_LATEST_NAVIGATIONAL_WARNINGS = "FetchLatestNavigationalWarningTaskTag"
   }
}