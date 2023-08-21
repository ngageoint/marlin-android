package mil.nga.msi.datasource.dgpsstation

import mil.nga.msi.datasource.bookmark.Bookmark

data class DgpsStationWithBookmark(
   val dgpsStation: DgpsStation,
   val bookmark: Bookmark?
)