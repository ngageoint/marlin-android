package mil.nga.msi.repository.asam

import android.app.Application
import android.util.Log
import androidx.lifecycle.map
import androidx.work.*
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.asam.LoadAsamWorker
import mil.nga.msi.work.asam.RefreshAsamWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsamRepository @Inject constructor(
   val application: Application,
   private val workManager: WorkManager,
   private val localDataSource: AsamLocalDataSource,
   private val remoteDataSource: AsamRemoteDataSource,
   private val notification: MarlinNotification,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   val asams = localDataSource.observeAsams()
   val asamMapItems = localDataSource.observeAsamMapItems()
   fun getAsamListItems() = localDataSource.observeAsamListItems()

   fun observeAsam(reference: String) = localDataSource.observeAsam(reference)
   suspend fun getAsam(reference: String) = localDataSource.getAsam(reference)

   fun getAsams(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getAsams(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun fetchAsams(refresh: Boolean = false): List<Asam> {
      if (refresh) {
         val asams = remoteDataSource.fetchAsams()

         val fetched = userPreferencesRepository.fetched(DataSource.ASAM)
         Log.i("billy", "fetched ASAMs $fetched")
         if (fetched != null) {
            Log.i("billy", "send ASAM notification because $fetched")
            val newAsams = asams.subtract(localDataSource.existingAsams(asams.map { it.reference }).toSet()).toList()
            notification.asam(newAsams)
         }

         localDataSource.insert(asams)
      }

      return localDataSource.getAsams()
   }

   fun fetchAsams() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadAsamWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshAsamWorker::class.java)
         .setConstraints(
            Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_ASAMS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   fun fetchAsamsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshAsamWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_ASAMS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_ASAMS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_ASAMS_TASK).map { workInfo ->
      workInfo.first()?.state == WorkInfo.State.RUNNING
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_ASAMS_TASK = "FetchLatestAsamsTask"
      private const val TAG_FETCH_LATEST_ASAMS = "FetchLatestAsamsTaskTag"
   }
}