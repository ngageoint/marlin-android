package mil.nga.msi.datasource.navigationwarning

import mil.nga.msi.datasource.bookmark.Bookmark

data class NavigationalWarningWithBookmark(
   val navigationalWarning: NavigationalWarning,
   val bookmark: Bookmark?
)

data class NavigationalWarningListItemWithBookmark(
   val navigationalWarning: NavigationalWarningListItem,
   val bookmark: Bookmark?
)