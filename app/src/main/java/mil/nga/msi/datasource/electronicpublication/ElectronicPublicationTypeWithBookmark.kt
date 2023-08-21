package mil.nga.msi.datasource.electronicpublication

import mil.nga.msi.datasource.bookmark.Bookmark

data class ElectronicPublicationTypeWithBookmark(
   val electronicPublicationType: ElectronicPublicationType,
   val bookmark: Bookmark? = null
)