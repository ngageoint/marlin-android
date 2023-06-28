package mil.nga.msi.datasource.asam

import mil.nga.msi.datasource.bookmark.Bookmark

data class AsamWithBookmark(
   val asam: Asam,
   val bookmark: Bookmark?
)