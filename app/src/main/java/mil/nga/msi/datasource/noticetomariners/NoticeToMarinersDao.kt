package mil.nga.msi.datasource.noticetomariners

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.light.Light

@Dao
interface NoticeToMarinersDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(noticeToMariners: NoticeToMariners)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(noticeToMariners: List<NoticeToMariners>): List<Long>

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(noticeToMariners: NoticeToMariners): Int

   @Query("SELECT COUNT(*) from notice_to_mariners")
   fun count(): Int

   @Query("SELECT * FROM notice_to_mariners")
   fun observeNoticeToMariners(): PagingSource<Int, NoticeToMarinersListItem>

   @Query("SELECT * FROM notice_to_mariners ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestNoticeToMariners(): NoticeToMariners?

//   @Query("SELECT * FROM asams WHERE reference = :reference")
//   fun observeAsam(reference: String): LiveData<Asam>

   @Query("SELECT * FROM notice_to_mariners")
   suspend fun getNoticeToMariners(): List<NoticeToMariners>

   @Query("SELECT * FROM notice_to_mariners WHERE notice_number = :noticeNumber")
   suspend fun getNoticeToMariners(noticeNumber: Int): List<NoticeToMariners>
//
//   @RawQuery(observedEntities = [Asam::class])
//   @RewriteQueriesToDropUnusedColumns
//   fun getAsams(query: SupportSQLiteQuery): List<Asam>
//
//   @Query("SELECT * FROM asams WHERE reference = :reference")
//   suspend fun getAsam(reference: String): Asam?
//
   @Query("SELECT * FROM notice_to_mariners ORDER BY notice_number DESC")
   fun getNoticeToMarinersListItems(): Flow<List<NoticeToMariners>>
//
//   @RawQuery(observedEntities = [Asam::class])
//   @RewriteQueriesToDropUnusedColumns
//   fun observeAsamMapItems(query: SupportSQLiteQuery): Flow<List<AsamMapItem>>

   @Query("SELECT * FROM notice_to_mariners WHERE ods_entry_id IN (:odsEntryIds)")
   suspend fun existingNoticeToMariners(odsEntryIds: List<Int>): List<NoticeToMariners>
}