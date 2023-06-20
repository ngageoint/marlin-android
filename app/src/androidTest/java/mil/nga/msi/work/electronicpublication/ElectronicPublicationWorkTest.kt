package mil.nga.msi.work.electronicpublication

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationLocalDataSource
import org.junit.After
import org.junit.Test

class LoadDgpsStationWorkerFactory(private val dataSource: ElectronicPublicationLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
      return LoadElectronicPublicationsWorker(appContext, workerParameters, dataSource)
   }
}

class DgpsStationWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_electronic_publications() {
      val mockDataSource = mockk<ElectronicPublicationLocalDataSource>()
      coEvery { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadElectronicPublicationsWorker>(context)
         .setWorkerFactory(LoadDgpsStationWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_electronic_publications() {
      val mockDataSource = mockk<ElectronicPublicationLocalDataSource>()
      coEvery { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadElectronicPublicationsWorker>(context)
         .setWorkerFactory(LoadDgpsStationWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}