package mil.nga.msi.datasource.noticetomariners

data class NoticeToMarinersGraphics(
   val noticeNumber: Int
) {
   var noticeYear: Int? = null
   var noticeWeek: Int? = null
   var chartNumber: String? = null
   var priceCategory: String? = null
   var subregion: Int? = null
   var graphicType: String? = null
   var seqNum: Int? = null
   var fileName: String? = null
   var fileSize: Int? = null
}