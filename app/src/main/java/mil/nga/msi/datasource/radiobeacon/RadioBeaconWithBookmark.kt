package mil.nga.msi.datasource.radiobeacon

import mil.nga.msi.datasource.bookmark.Bookmark

data class RadioBeaconWithBookmark(
   val radioBeacon: RadioBeacon,
   val bookmark: Bookmark?
)