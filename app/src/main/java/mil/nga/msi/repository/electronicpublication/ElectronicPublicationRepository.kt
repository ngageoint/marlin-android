package mil.nga.msi.repository.electronicpublication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import javax.inject.Inject


class ElectronicPublicationRepository @Inject constructor(
    private val localData: ElectronicPublicationLocalDataSource
) {

    suspend fun syncFromRemote(): Unit {
        TODO()
    }

    fun observeElectronicPublications(): Flow<List<ElectronicPublication>> {
        TODO()
    }

    /**
     * Query for publications of the given type.  The results are in ascending order by
     * [ElectronicPublication.pubDownloadOrder], [ElectronicPublication.sectionOrder],
     * [ElectronicPublication.s3Key] for consistency.
     */
    fun observeElectronicPublicationsOfType(pubType: ElectronicPublicationType): Flow<List<ElectronicPublication>> {
        return localData.observePublicationsOfType(pubType)
    }

    fun observeFileCountsByType(): Flow<Map<ElectronicPublicationType, Int>> = localData.observeFileCountsByType()

    val fetching: LiveData<Boolean> = MutableLiveData(false)
}