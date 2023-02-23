package mil.nga.msi.datasource.noticetomariners

import java.time.Instant

data class Correction(
   val action: String?,
   val text: String?
)

data class ChartCorrection(
   val chartId: Int,
   val chartNumber: String,
   val internationalNumber: String? = null,
   val starred: Boolean? = null,
   val limitedDistribution: Boolean? = null,
   val correctionType: String? = null,
   val currentNoticeNumber: String? = null,
   val noticeAction: String? = null,
   val editionNumber: String? = null,
   val editionDate: String? = null,
   val lastNoticeNumber: String? = null,
   val corrections: List<Correction> = emptyList(),
   val authority: String? = null,
   val region: String? = null,
   val subregion: String? = null,
   val portCode: Int? = null,
   val classification: String? = null,
   val priceCategory: String? = null,
   val date: Instant? = null,
   val location: String? = null
) {
   val noticeWeek: Int =
      currentNoticeNumber?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0

   val noticeDecade: Int =
      currentNoticeNumber?.split("/")?.getOrNull(1)?.toIntOrNull() ?: 0

   val noticeYear: Int
      get()  {
         return if (noticeDecade > 50) noticeDecade + 1900 else noticeDecade + 2000
      }

   val noticeNumber: Int = "${noticeYear}$noticeWeek".toInt()
}
