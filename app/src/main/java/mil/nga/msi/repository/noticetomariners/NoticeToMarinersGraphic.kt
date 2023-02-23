package mil.nga.msi.repository.noticetomariners

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics

@Serializable
@Parcelize
data class NoticeToMarinersGraphic(
   val noticeNumber: Int,
   val chartNumber: String?,
   val graphicType: String?,
   val fileName: String
): Parcelable {
   @IgnoredOnParcel
   val title = "$graphicType $chartNumber"

   @IgnoredOnParcel
   val resourceType = when (graphicType) {
      "Depth Tab" -> "depthtabs"
      "Note" -> "notes"
      else -> "chartlets"
   }

   @IgnoredOnParcel
   val key = "16920957/SFH00000/UNTM/$noticeNumber/$resourceType/$fileName"

   @IgnoredOnParcel
   val url = "https://msi.nga.mil/api/publications/download?type=view&key=$key"

   companion object {
      fun fromNoticeToMarinersGraphics(graphics: NoticeToMarinersGraphics): NoticeToMarinersGraphic {
         return NoticeToMarinersGraphic(graphics.noticeNumber, graphics.chartNumber, graphics.graphicType, graphics.fileName)
      }
   }
}