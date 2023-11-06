package mil.nga.msi.datasource.electronicpublication

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectronicPublicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: ElectronicPublication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: List<ElectronicPublication>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(x: ElectronicPublication)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(allThese: List<ElectronicPublication>)

    @Query("select count(*) from epubs")
    suspend fun count(): Int

    @Query("select pub_type_id, count(pub_type_id) as file_count from epubs group by pub_type_id")
    @MapInfo(keyColumn = "pub_type_id", valueColumn = "file_count")
    @TypeConverters(ElectronicPublicationTypeConverters::class)
    fun observeFileCountsByType(): Flow<Map<ElectronicPublicationType, Int>>

    @Query("select * from epubs order by s3_key asc")
    suspend fun readElectronicPublications(): List<ElectronicPublication>

    @Query("select * from epubs where s3_key = :s3Key")
    suspend fun findById(s3Key: String): ElectronicPublication?

    @Query("select * from epubs where is_downloading order by s3_key asc")
    suspend fun findDownloadingElectronicPublications(): List<ElectronicPublication>

    @Query("select * from epubs where s3_key = :s3Key")
    suspend fun getElectronicPublication(s3Key: String): ElectronicPublication?

    @Query("select * from epubs order by s3_key asc")
    @RewriteQueriesToDropUnusedColumns
    fun observeElectronicPublications(): PagingSource<Int, ElectronicPublicationListItem>

    @Query("select * from epubs where pub_type_id = :typeId order by pub_download_order asc, section_order asc, s3_key asc")
    fun observeElectronicPublicationsOfType(typeId: Int): Flow<List<ElectronicPublication>>

    @Query("select * from epubs where s3_key = :s3Key")
    fun observeElectronicPublication(s3Key: String): Flow<ElectronicPublication>

    @Transaction
    suspend fun beginDownloading(ePub: ElectronicPublication, beginDownload: suspend (ElectronicPublication) -> ElectronicPublication) {
        val latest = findById(ePub.s3Key)
        if (latest == null || latest.isDownloading || latest.isDownloaded) {
            return
        }
        val preDownload = latest.copy(isDownloading = true)
        update(preDownload)
        val downloading = beginDownload(preDownload)
        update(downloading)
    }

    @Transaction
    suspend fun updateDownloadProgress(progressUpdates: suspend (List<ElectronicPublication>) -> List<ElectronicPublication>) {
        val downloadingEPubs = findDownloadingElectronicPublications()
        val updates = progressUpdates(downloadingEPubs)
        update(updates)
    }

    @Transaction
    suspend fun updateToRemoveDownload(ePub: ElectronicPublication, removeDownloadedFile: suspend () -> Unit) {
        val latest = findById(ePub.s3Key) ?: return
        val scrubDownloadUpdate = latest.copy(isDownloading = false, isDownloaded = false,
            downloadedBytes = 0, localDownloadId = null, localDownloadRelPath = null)
        update(scrubDownloadUpdate)
        removeDownloadedFile()
    }
}

class ElectronicPublicationTypeConverters {

    @TypeConverter
    fun publicationTypeFromTypeCode(typeCode: Int) = ElectronicPublicationType.fromTypeCode(typeCode)
}