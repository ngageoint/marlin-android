//package mil.nga.msi.work.dgpsstation
//
//import android.content.Context
//import androidx.test.espresso.matcher.ViewMatchers.assertThat
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.work.ListenableWorker
//import androidx.work.WorkerFactory
//import androidx.work.WorkerParameters
//import androidx.work.testing.TestListenableWorkerBuilder
//import kotlinx.coroutines.runBlocking
//import mil.nga.msi.MarlinNotification
//import mil.nga.msi.repository.asam.AsamRepository
//import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
//import mil.nga.msi.repository.preferences.UserPreferencesRepository
//import mil.nga.msi.work.asam.RefreshAsamWorker
//import org.hamcrest.Matchers.`is`
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.ArgumentMatchers.anyList
//import org.mockito.Mockito
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.never
//import org.mockito.kotlin.verify
//
//class LoadAsamWorkerFactory(private val dataSource: DgpsStationLocalDataSource) : WorkerFactory() {
//   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
//      return LoadDgpsStationWorker(appContext, workerParameters, dataSource)
//   }
//}
//
//class RefreshAsamWorkerFactory(
//   private val repository: AsamRepository,
//   private val userPreferencesRepository: UserPreferencesRepository,
//   private val notification: MarlinNotification
//) : WorkerFactory() {
//   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
//      return RefreshAsamWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
//   }
//}
//
//@RunWith(AndroidJUnit4::class)
//class DgpsStationWorkTest {
//
//   @Test
//   fun should_load_dgps_stations() {
//      val mockDataSource = mock<DgpsStationLocalDataSource>()
//      Mockito.`when`(mockDataSource.isEmpty()).thenReturn(true)
//
//      val context = InstrumentationRegistry.getInstrumentation().targetContext
//
//      val worker = TestListenableWorkerBuilder<LoadDgpsStationWorker>(context)
//         .setWorkerFactory(LoadAsamWorkerFactory(mockDataSource))
//         .build()
//
//      runBlocking {
//         val result = worker.doWork()
//         verify(mockDataSource).insert(anyList())
//         assertThat(result, `is`(ListenableWorker.Result.success()))
//      }
//   }
//
//   @Test
//   fun should_not_load_dgps_stations() {
//      val mockDataSource = mock<DgpsStationLocalDataSource>()
//      Mockito.`when`(mockDataSource.isEmpty()).thenReturn(false)
//
//      val context = InstrumentationRegistry.getInstrumentation().targetContext
//
//      val worker = TestListenableWorkerBuilder<LoadDgpsStationWorker>(context)
//         .setWorkerFactory(LoadAsamWorkerFactory(mockDataSource))
//         .build()
//
//      runBlocking {
//         val result = worker.doWork()
//         verify(mockDataSource, never()).insert(anyList())
//         assertThat(result, `is`(ListenableWorker.Result.success()))
//      }
//   }
//}