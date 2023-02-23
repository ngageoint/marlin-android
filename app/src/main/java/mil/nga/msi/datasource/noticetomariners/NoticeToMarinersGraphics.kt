package mil.nga.msi.datasource.noticetomariners

data class NoticeToMarinersGraphics(
   val noticeNumber: Int,
   var chartNumber: String,
   var fileName: String
) {
   var noticeYear: Int? = null
   var noticeWeek: Int? = null
   var priceCategory: String? = null
   var subregion: Int? = null
   var graphicType: String? = null
   var seqNum: Int? = null
   var fileSize: Int? = null
}