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
    private val localData: ElectronicPublicationLocalDataSource,
    private val downloadManager: DownloadManager,
    private val context: Application,
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

    suspend fun download(ePub: ElectronicPublication) {
        localData.beginDownloading(ePub) { preDownloadEPub ->
            val downloadUrl = "https://msi.nga.mil/api/publications/download?type=download&key=${ePub.s3Key}"
            val destRelPath = "electronic_publications/${ePub.pubTypeId}/${ePub.fullFilename ?: "${ePub.s3Key}.${ePub.fileExtension ?: "data"}"}"
            val request = DownloadManager.Request(downloadUrl.toUri()).setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, destRelPath)
            val downloadId = downloadManager.enqueue(request)
            preDownloadEPub.copy(localDownloadId = downloadId, localDownloadRelPath = destRelPath)
        }
    }

    suspend fun removeDownload(ePub: ElectronicPublication) {
        if (ePub.localDownloadId == null) {
            return
        }
        localData.updateToRemoveDownload(ePub, { downloadManager.remove(ePub.localDownloadId) })
    }

    suspend fun cancelDownload(ePub: ElectronicPublication) {
        removeDownload(ePub)
    }

    /**
     * Query the downloads in progress and update their status in the local database.  The
     * expectation is that the caller is concurrently [observing][observeElectronicPublicationsOfType]
     * receive the updates.
     */
    suspend fun updateDownloadProgress() {
        localData.updateDownloadProgress() { downloadingEPubs ->
            val ePubsByDownloadId = downloadingEPubs.associateBy { it.localDownloadId }
            val downloadIds = downloadingEPubs.map({ it.localDownloadId!! })
            val downloadIdsArray = LongArray(downloadIds.size)
            downloadIds.forEachIndexed { pos, id -> downloadIdsArray[pos] = id }
            var downloadQuery = DownloadManager.Query()
            if (downloadIds.size > 0) {
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
                        Log.d("ElectronicPublicationRepository", "downloading publication ${remoteUri} to ${localUri}")
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

    fun getContentUriToSharePublication(ePub: ElectronicPublication): Uri {
        val extDirPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val ePubFile = File(extDirPath, ePub.localDownloadRelPath)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", ePubFile)
    }

    val fetching: LiveData<Boolean> = MutableLiveData(false)
}