package mil.nga.msi.network.noticetomariners

import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming


interface NoticeToMarinersService {
   @GET("/api/publications/ntm/pubs")
   suspend fun getNoticeToMariners(
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
   ): Response<List<NoticeToMariners>>

   @GET("/api/publications/ntm/ntm-graphics")
   suspend fun getNoticeToMarinersGraphics(
      @Query("noticeNumber") noticeNumber: Int,
      @Query("graphicType") graphicType: String = "All",
      @Query("output") output: String = "json"
   ): Response<List<NoticeToMarinersGraphics>>
   
   @GET("/api/publications/download/")
   @Streaming
   suspend fun getNoticeToMarinersGraphic(
      @Query("type") type: String = "download",
      @Query("key") key: String,
   ): Response<ResponseBody>
}