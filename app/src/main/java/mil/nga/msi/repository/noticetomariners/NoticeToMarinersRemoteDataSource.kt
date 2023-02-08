package mil.nga.msi.repository.noticetomariners

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.network.noticetomariners.NoticeToMarinersService
import java.io.File
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.inject.Inject

class NoticeToMarinersRemoteDataSource @Inject constructor(
   private val application: Application,
   private val service: NoticeToMarinersService,
   private val localDataSource: NoticeToMarinersLocalDataSource
) {
   suspend fun fetchNoticeToMariners(): List<NoticeToMariners> {
      val noticeToMariners = mutableListOf<NoticeToMariners>()

      val latestNoticeToMariners = localDataSource.getLatestNoticeToMariners()
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestNoticeToMariners != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         val minYear = latestNoticeToMariners.noticeNumber.toString().take(4)
         val minWeek = latestNoticeToMariners.noticeNumber.toString().takeLast(2)

         minNoticeNumber = "${minYear}${"%02d".format(minWeek.toInt() + 1)}"
         maxNoticeNumber = "${year}${"%02d".format(week + 1)}"
      }

      val response = service.getNoticeToMariners(
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (response.isSuccessful) {
         val body = response.body()
         body?.let { noticeToMariners.addAll(it) }
      }

      return noticeToMariners
   }

   suspend fun fetchNoticeToMarinersGraphics(noticeNumber: Int): List<NoticeToMarinersGraphics> {
      val graphics = mutableListOf<NoticeToMarinersGraphics>()

      val response = service.getNoticeToMarinersGraphics(
         noticeNumber = noticeNumber
      )

      if (response.isSuccessful) {
         val body = response.body()
         body?.let { graphics.addAll(it) }
      }

      return graphics
   }

   suspend fun fetchNoticeToMarinersPublication(notice: NoticeToMariners): Uri = withContext(Dispatchers.IO) {
      val response = service.getNoticeToMarinersPublication(key = notice.odsKey)
      val cacheFile = NoticeToMariners.cachePath(application, notice.filename)
      Files.createDirectories(cacheFile)
      if (response.isSuccessful) {
         response.body()?.byteStream()?.use { input ->
            Files.copy(input, cacheFile, StandardCopyOption.REPLACE_EXISTING)
         }
      }

      // Done streaming from server, move to non cache directory
      val file = NoticeToMariners.filesPath(application, notice.filename)
      Files.createDirectories(file)
      Files.copy(cacheFile, file, StandardCopyOption.REPLACE_EXISTING)
      Files.delete(cacheFile)

      getUriForFile(application, "${application.packageName}.fileprovider", file.toFile())
   }

   suspend fun fetchNoticeToMarinersGraphic(graphic: NoticeToMarinersGraphic): Uri = withContext(Dispatchers.IO) {
      val response = service.getNoticeToMarinersGraphic(key = graphic.key)
      val directory = Paths.get(application.cacheDir.absolutePath, "notice_to_mariners")
      Files.createDirectories(directory)
      val file = File(directory.toFile(), graphic.fileName)
      if (response.isSuccessful) {
         response.body()?.byteStream()?.use { input ->
            file.outputStream().use { output ->
               input.copyTo(output)
            }
         }
      }

      getUriForFile(application, "${application.packageName}.fileprovider", file)
   }
}