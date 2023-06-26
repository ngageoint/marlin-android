package mil.nga.msi.ui.action

import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.bookmark.BookmarkKey

sealed class ModuAction(): Action() {
   class Location(val text: String): ModuAction()
   class Share(val modu: Modu) : ModuAction()
   class Zoom(val modu: Modu): ModuAction()
   class Bookmark(val bookmark: BookmarkKey): ModuAction()
}