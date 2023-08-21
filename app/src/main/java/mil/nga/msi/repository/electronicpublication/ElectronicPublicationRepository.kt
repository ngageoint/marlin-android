package mil.nga.msi.repository.electronicpublication

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import java.io.File
import javax.inject.Inject

class ElectronicPublicationRepository @Inject constructor(
    private val localDataSource: ElectronicPublicationLocalDataSource,
    private val downloadManager: DownloadManager,
    private val context: Application,
) {
    suspend fun getElectronicPublication(s3Key: String) = localDataSource.getElectronicPublication(s3Key)

    /**
     * Query for publications of the given type.  The results are in ascending order by
     * [ElectronicPublication.pubDownloadOrder], [ElectronicPublication.sectionOrder],
     * [ElectronicPublication.s3Key] for consistency.
     */
    fun observeElectronicPublicationsOfType(publicationType: ElectronicPublicationType): Flow<List<ElectronicPublication>> {
        return localDataSource.observePublicationsOfType(publicationType)
    }

    fun observeElectronicPublication(s3Key: String) = localDataSource.observeElectronicPublication(s3Key)

    fun observeFileCountsByType(): Flow<Map<ElectronicPublicationType, Int>> = localDataSource.observeFileCountsByType()

    suspend fun download(publication: ElectronicPublication) {
        localDataSource.beginDownloading(publication) { preDownloadEPub ->
            val downloadUrl = "https://msi.nga.mil/api/publications/download?type=download&key=${publication.s3Key}"
            val destRelPath = "electronic_publications/${publication.pubTypeId}/${publication.fullFilename ?: "${publication.s3Key}.${publication.fileExtension ?: "data"}"}"
            val request = DownloadManager.Request(downloadUrl.toUri()).setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, destRelPath)
            val downloadId = downloadManager.enqueue(request)
            preDownloadEPub.copy(localDownloadId = downloadId, localDownloadRelPath = destRelPath)
        }
    }

    suspend fun removeDownload(publication: ElectronicPublication) {
        publication.localDownloadId?.let { downloadId ->
            localDataSource.updateToRemoveDownload(publication) { downloadManager.remove(downloadId) }
        }
    }

    suspend fun cancelDownload(publication: ElectronicPublication) = removeDownload(publication)

    /**
     * Query the downloads in progress and update their status in the local database.  The
     * expectation is that the caller is concurrently [observing][observeElectronicPublicationsOfType]
     * receive the updates.
     */
    suspend fun updateDownloadProgress() {
        localDataSource.updateDownloadProgress() { downloadingEPubs ->
            val ePubsByDownloadId = downloadingEPubs.associateBy { it.localDownloadId }
            val downloadIds = downloadingEPubs.map { it.localDownloadId!! }
            val downloadIdsArray = LongArray(downloadIds.size)
            downloadIds.forEachIndexed { pos, id -> downloadIdsArray[pos] = id }
            var downloadQuery = DownloadManager.Query()
            if (downloadIds.isNotEmpty()) {
                downloadQuery = downloadQuery.setFilterById(*downloadIdsArray)
            }
            val downloads = downloadManager.query(downloadQuery)
            val updates = mutableListOf<ElectronicPublication>()
            val colDownloadId = downloads.getColumnIndex(DownloadManager.COLUMN_ID)
            val colUri = downloads.getColumnIndex(DownloadManager.COLUMN_URI)
            val colLocalUri = downloads.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val colStatus = downloads.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val colBytes = downloads.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val colMediaType = downloads.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)
            val isEPubDownload = Regex("^https://msi.nga.mil/api/publications/download.+")
            downloads.use {
                while (downloads.moveToNext()) {
                    val remoteUri = downloads.getString(colUri)
                    if (isEPubDownload.matches(remoteUri)) {
                        val downloadId = downloads.getLong(colDownloadId)
                        val status = downloads.getInt(colStatus)
                        val bytes = downloads.getLong(colBytes)
                        val mediaType = downloads.getString(colMediaType)
                        val localUri = downloads.getString(colLocalUri)
                        Log.d("ElectronicPublicationRepository", "downloading publication $remoteUri to $localUri")
                        ePubsByDownloadId[downloadId]?.let { downloadingEPub ->
                            val update = when (status) {
                                DownloadManager.STATUS_FAILED -> downloadingEPub.copy(
                                    downloadedBytes = 0,
                                    isDownloading = false,
                                    isDownloaded = false,
                                    localDownloadRelPath = null,
                                    localDownloadId = null,
                                )
                                DownloadManager.STATUS_SUCCESSFUL -> downloadingEPub.copy(
                                    downloadedBytes = bytes,
                                    isDownloading = false,
                                    isDownloaded = true,
                                )
                                else -> downloadingEPub.copy(downloadedBytes = bytes, downloadMediaType = mediaType)
                            }
                            updates.add(update)
                        }
                    }
                }
            }
            updates
        }
    }

    fun getContentUriToSharePublication(publication: ElectronicPublication): Uri? {
        val extDirPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return publication.localDownloadRelPath?.let { path ->
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(extDirPath, path))
        }
    }

    val fetching: LiveData<Boolean> = MutableLiveData(false)
}