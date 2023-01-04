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

    @Query("select count(*) from epubs")
    suspend fun count(): Int

    @Query("select pub_type_id, count(pub_type_id) as file_count from epubs group by pub_type_id")
    @MapInfo(keyColumn = "pub_type_id", valueColumn = "file_count")
    @TypeConverters(ElectronicPublicationTypeConverters::class)
    fun observeFileCountsByType(): Flow<Map<ElectronicPublicationType, Int>>

    @Query("select * from epubs order by s3_key asc")
    suspend fun readElectronicPublications(): List<ElectronicPublication>
    @Query("select * from epubs order by s3_key asc")
    fun observeElectronicPublications(): PagingSource<Int, ElectronicPublicationListItem>
    @Query("select * from epubs where pub_type_id = :typeId order by pub_download_order asc, section_order asc, s3_key asc")
    fun observeElectronicPublicationsOfType(typeId: Int): Flow<List<ElectronicPublication>>
}

class ElectronicPublicationTypeConverters {

    @TypeConverter
    fun publicationTypeFromTypeCode(typeCode: Int) = ElectronicPublicationType.fromTypeCode(typeCode)
}