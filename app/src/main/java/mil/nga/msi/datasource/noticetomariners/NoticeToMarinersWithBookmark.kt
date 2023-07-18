package mil.nga.msi.datasource.noticetomariners

import mil.nga.msi.datasource.bookmark.Bookmark

data class NoticeToMarinersWithBookmark(
   val noticeNumber: Int,
   val bookmark: Bookmark?
)