package mil.nga.msi.datasource.electronicpublication

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface ElectronicPublicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: ElectronicPublication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: List<ElectronicPublication>)

    @Query("select count(*) from epubs")
    suspend fun count(): Int

    @Query("select * from epubs order by s3_key asc")
    suspend fun readElectronicPublications(): List<ElectronicPublication>

    @Query("select * FROM epubs order by s3_key asc")
    fun observeElectronicPublications(): PagingSource<Int, ElectronicPublicationListItem>
}