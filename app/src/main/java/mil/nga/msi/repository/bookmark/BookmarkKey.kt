package mil.nga.msi.repository.bookmark

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.modu.Modu

@Serializable
@Parcelize
data class BookmarkKey(
   val id: String,
   val dataSource: DataSource,
   val notes: String? = null
): Parcelable {
   companion object {
      fun fromAsam(asam: Asam): BookmarkKey {
         return BookmarkKey(asam.reference, DataSource.ASAM, asam.bookmarkNotes)
      }

      fun fromModu(modu: Modu): BookmarkKey {
         return BookmarkKey(modu.name, DataSource.MODU, modu.bookmarkNotes)
      }
   }
}