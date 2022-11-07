package mil.nga.msi.repository.light

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightListItem
import mil.nga.msi.datasource.light.LightMapItem
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.light.LoadLightWorker
import mil.nga.msi.work.light.RefreshLightWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LightRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: LightLocalDataSource,
   private val remoteDataSource: LightRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {

   @OptIn(ExperimentalCoroutinesApi::class)
   fun observeLightMapItems(): Flow<List<LightMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.LIGHT] ?: emptyList()
         val query = QueryBuilder("lights", filters).buildQuery()
         localDataSource.observeLightMapItems(query)
      }
   }

   fun observeLightListItems(filters: List<Filter>): PagingSource<Int, LightListItem> {
      val query = QueryBuilder("lights", filters).buildQuery()
      return localDataSource.observeLightListItems(query)
   }

   fun observeLight(
      volumeNumber: String,
      featureNumber: String,
   ) = localDataSource.observeLight(volumeNumber, featureNumber)

   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ) = localDataSource.getLight(volumeNumber, featureNumber, characteristicNumber)

   fun getLights(filters: List<Filter>): List<Light> {
      val query = QueryBuilder("lights", filters).buildQuery()
      return localDataSource.getLights(query)
   }

   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
      characteristicNumber: Int
   ) = localDataSource.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber)

   suspend fun fetchLights(refresh: Boolean = false): List<Light> {
      if (refresh) {
         val newLights = mutableListOf<Light>()
         val fetched = userPreferencesRepository.fetched(DataSource.LIGHT)

         PublicationVolume.values().forEach { volume ->
            val lights = remoteDataSource.fetchLights(volume)

            if (fetched == null) {
               newLights.addAll(lights.subtract(localDataSource.existingLights(lights.map { it.compositeKey() }).toSet()).toList())
            }

            localDataSource.insert(lights)
         }

         notification.light(newLights)
      }

      return localDataSource.getLights()
   }

   fun fetchLights() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadLightWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshLightWorker::class.java)
         .setConstraints(
            Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_LIGHTS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   fun fetchLightsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshLightWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_LIGHTS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_LIGHTS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_LIGHTS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_LIGHTS_TASK = "FetchLatestLightsTask"
      private const val TAG_FETCH_LATEST_LIGHTS = "FetchLatestLightsTaskTag"
   }
}