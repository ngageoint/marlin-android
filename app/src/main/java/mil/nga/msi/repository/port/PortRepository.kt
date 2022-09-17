package mil.nga.msi.repository.port

import androidx.lifecycle.map
import androidx.work.*
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.work.port.RefreshPortWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PortRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: PortLocalDataSource,
   private val remoteDataSource: PortRemoteDataSource,
   private val notification: MarlinNotification
) {
   val portMapItems = localDataSource.observePortMapItems()
   fun getPortListItems() = localDataSource.observePortListItems()

   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)

   fun observePort(portNumber: Int) = localDataSource.observePort(portNumber)
   suspend fun getPort(portNumber: Int) = localDataSource.getPort(portNumber)

   suspend fun fetchPorts(refresh: Boolean = false): List<Port> {
      if (refresh) {
         val ports = remoteDataSource.fetchPorts()

         if (!localDataSource.isEmpty()) {
            val newPorts = ports.subtract(localDataSource.existingPorts(ports.map { it.portNumber }).toSet()).toList()
            notification.port(newPorts)
         }

         localDataSource.insert(ports)
      }

      return localDataSource.getPorts()
   }

   fun fetchPorts() {
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshPortWorker::class.java)
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
            FETCH_LATEST_PORTS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchPortsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshPortWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_PORTS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_PORTS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_PORTS_TASK).map { workInfo ->
      workInfo.first()?.state == WorkInfo.State.RUNNING
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_PORTS_TASK = "FetchLatestPortsTask"
      private const val TAG_FETCH_LATEST_PORTS = "FetchLatestPortsTaskTag"
   }
}