package mil.nga.msi.datasource.electronicpublication

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface ElectronicPublicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: ElectronicPublication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(x: List<ElectronicPublication>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(x: ElectronicPublication): Int

    @Query("SELECT COUNT(*) from epubs")
    fun count(): Int

    @Query("SELECT * FROM epubs")
    fun observeElectronicPublications(): PagingSource<Int, ElectronicPublicationListItem>
}