package mil.nga.msi.datasource.port

import mil.nga.msi.datasource.bookmark.Bookmark

data class PortWithBookmark(
   val port: Port,
   val bookmark: Bookmark?
)