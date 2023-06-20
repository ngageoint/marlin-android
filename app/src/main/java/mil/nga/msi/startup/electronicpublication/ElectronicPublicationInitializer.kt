package mil.nga.msi.startup.electronicpublication

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.electronicpublication.LoadElectronicPublicationsWorker
import javax.inject.Inject

class ElectronicPublicationInitializer: Initializer<Unit> {

    @Inject
    lateinit var repository: ElectronicPublicationRepository
    @Inject
    lateinit var workManager: WorkManager

    override fun create(context: Context) {
        // Inject Hilt dependencies
        AppInitializer.resolve(context).inject(this)
        val seedRequest = OneTimeWorkRequest.Builder(LoadElectronicPublicationsWorker::class.java).build()
        workManager.enqueueUniqueWork(LoadElectronicPublicationsWorker.SEED_ELECTRONIC_PUBLICATIONS_WORK, ExistingWorkPolicy.KEEP, seedRequest)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(WorkManagerInitializer::class.java)
    }
}