package mil.nga.msi.datasource.electronicpublication

import mil.nga.msi.datasource.bookmark.Bookmark

data class ElectronicPublicationWithBookmark(
   val electronicPublication: ElectronicPublication,
   val bookmark: Bookmark? = null
)