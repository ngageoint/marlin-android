package mil.nga.msi.datasource.noticetomariners

import androidx.room.ColumnInfo

data class NoticeToMarinersListItem(
   @ColumnInfo(name = "ods_entry_id") val odsEntryId: Int,
   @ColumnInfo(name = "notice_number") val noticeNumber: String,
)