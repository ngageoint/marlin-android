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
   @RewriteQueriesToDropUnusedColumns
   fun observeNoticeToMariners(): PagingSource<Int, NoticeToMarinersListItem>

   @Query("SELECT * FROM notice_to_mariners ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestNoticeToMariners(): NoticeToMariners?

   @Query("SELECT * FROM notice_to_mariners")
   suspend fun getNoticeToMariners(): List<NoticeToMariners>

   @Query("SELECT * FROM notice_to_mariners WHERE notice_number = :noticeNumber")
   suspend fun getNoticeToMariners(noticeNumber: Int): List<NoticeToMariners>

   @Query("SELECT * FROM notice_to_mariners ORDER BY notice_number DESC")
   fun getNoticeToMarinersListItems(): Flow<List<NoticeToMariners>>

   @Query("SELECT * FROM notice_to_mariners WHERE ods_entry_id IN (:odsEntryIds)")
   suspend fun existingNoticeToMariners(odsEntryIds: List<Int>): List<NoticeToMariners>
}