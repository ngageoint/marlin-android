package mil.nga.msi.startup.electronicpublication

import android.content.Context
import androidx.startup.Initializer
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import mil.nga.msi.startup.WorkManagerInitializer
import javax.inject.Inject

class ElectronicPublicationInitializer: Initializer<Unit> {

    @Inject
    lateinit var repository: ElectronicPublicationRepository

    override fun create(context: Context): Unit {
        // Inject Hilt dependencies
        AppInitializer.resolve(context).inject(this)
//        repository.fetchElectronicPublications()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(WorkManagerInitializer::class.java)
    }
}