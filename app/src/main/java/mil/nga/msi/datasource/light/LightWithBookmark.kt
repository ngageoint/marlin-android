package mil.nga.msi.datasource.light

import mil.nga.msi.datasource.bookmark.Bookmark

data class LightWithBookmark(
   val light: Light,
   val bookmark: Bookmark?
)

data class LightsWithBookmark(
   val lights: List<Light>,
   val bookmark: Bookmark?
)