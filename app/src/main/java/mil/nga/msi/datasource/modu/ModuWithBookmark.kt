package mil.nga.msi.datasource.modu

import mil.nga.msi.datasource.bookmark.Bookmark

data class ModuWithBookmark(
   val modu: Modu,
   val bookmark: Bookmark?
)