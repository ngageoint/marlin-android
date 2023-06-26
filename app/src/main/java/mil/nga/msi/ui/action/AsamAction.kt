package mil.nga.msi.ui.action

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.bookmark.BookmarkKey

sealed class AsamAction(): Action() {
   class Location(val text: String): AsamAction()
   class Share(val asam: Asam) : AsamAction()
   class Zoom(val asam: Asam): AsamAction()
   class Bookmark(val bookmark: BookmarkKey): AsamAction()
}


