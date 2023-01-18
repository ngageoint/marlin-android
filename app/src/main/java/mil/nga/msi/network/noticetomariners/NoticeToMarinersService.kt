package mil.nga.msi.network.noticetomariners

import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeToMarinersService {
   @GET("/api/publications/ntm/pubs")
   suspend fun getNoticeToMariners(
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
   ): Response<List<NoticeToMariners>>
}