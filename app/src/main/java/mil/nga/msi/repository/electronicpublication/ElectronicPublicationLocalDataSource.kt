package mil.nga.msi.repository.electronicpublication

import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationDao
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import javax.inject.Inject

class ElectronicPublicationLocalDataSource @Inject constructor(private val dao: ElectronicPublicationDao) {

    suspend fun count(): Int = dao.count()
    suspend fun isEmpty(): Boolean = count() == 0
    suspend fun insert(allThese: List<ElectronicPublication>) = dao.insert(allThese)
    suspend fun update(ePub: ElectronicPublication) = dao.update(ePub)
    /**
     * Mark the given publication as downloading in the local data store, begin the download by
     * executing the given lambda, then update the publication record again with the result from
     * the block, all in a transaction.
     */
    suspend fun beginDownloading(ePub: ElectronicPublication, beginDownload: (ElectronicPublication) -> ElectronicPublication) = dao.beginDownloading(ePub, beginDownload)

    /**
     * Query the local data store for publications that are currently downloading, pass them to the
     * given lambda to check for download status.
     */
    suspend fun updateDownloadProgress(progressUpdates: (List<ElectronicPublication>) -> List<ElectronicPublication>) = dao.updateDownloadProgress(progressUpdates)
    suspend fun updateToRemoveDownload(ePub: ElectronicPublication, removeDownloadedFile: suspend () -> Unit) = dao.updateToRemoveDownload(ePub, removeDownloadedFile)
    fun observeFileCountsByType() = dao.observeFileCountsByType()
    fun observePublicationsOfType(pubType: ElectronicPublicationType) = dao.observeElectronicPublicationsOfType(pubType.typeId)
}