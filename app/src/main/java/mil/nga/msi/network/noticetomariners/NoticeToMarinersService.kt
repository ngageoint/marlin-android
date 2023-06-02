package mil.nga.msi.network.noticetomariners

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface NoticeToMarinersService {
   @GET("/api/publications/ntm/pubs")
   suspend fun getNoticeToMariners(
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
   ): Response<NoticeToMarinersResponse>

   @GET("/api/publications/ntm/ntm-graphics")
   suspend fun getNoticeToMarinersGraphics(
      @Query("noticeNumber") noticeNumber: Int,
      @Query("graphicType") graphicType: String = "All",
      @Query("output") output: String = "json"
   ): Response<NoticeToMarinersGraphicsResponse>

   @GET("/api/publications/download/")
   @Streaming
   suspend fun getNoticeToMarinersPublication(
      @Query("type") type: String = "download",
      @Query("key") key: String,
   ): Response<ResponseBody>
   
   @GET("/api/publications/download/")
   @Streaming
   suspend fun getNoticeToMarinersGraphic(
      @Query("type") type: String = "download",
      @Query("key") key: String,
   ): Response<ResponseBody>

   @GET("/api/publications/ntm/ntm-chart-corr/geo")
   @Streaming
   suspend fun getNoticeToMarinersCorrections(
      @Query("latitudeLeft") minLatitude: Double,
      @Query("longitudeLeft") minLongitude: Double,
      @Query("latitudeRight") maxLatitude: Double,
      @Query("longitudeRight") maxLongitude: Double,
      @Query("noticeNumber") noticeNumber: Int? = null,
      @Query("output") output: String = "json"
   ): Response<ChartCorrectionResponse>
}