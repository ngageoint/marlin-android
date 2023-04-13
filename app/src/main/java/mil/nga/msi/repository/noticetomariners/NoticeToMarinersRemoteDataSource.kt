package mil.nga.msi.repository.noticetomariners

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.filter.Filter
import mil.nga.msi.network.noticetomariners.NoticeToMarinersService
import mil.nga.sf.util.GeometryUtils
import java.io.File
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
         body?.let { noticeToMariners.addAll(it.noticeToMariners) }
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
         body?.let { graphics.addAll(it.graphics) }
      }

      return graphics
   }

   suspend fun fetchNoticeToMarinersPublication(notice: NoticeToMariners): Uri? =
      withContext(Dispatchers.IO) {
         try {
            val response = service.getNoticeToMarinersPublication(key = notice.odsKey)
            val cacheFile = NoticeToMariners.cachePath(application, notice.filename)
            Files.createDirectories(cacheFile.parent)

            if (response.isSuccessful) {
               response.body()?.byteStream()?.use { input ->
                  Files.copy(input, cacheFile, StandardCopyOption.REPLACE_EXISTING)
               }

               // Done streaming from server, move to non cache directory
               val file = NoticeToMariners.externalFilesPath(application, notice.filename)
               Files.createDirectories(file.parent)
               Files.copy(cacheFile, file, StandardCopyOption.REPLACE_EXISTING)
               Files.delete(cacheFile)

               getUriForFile(application, "${application.packageName}.fileprovider", file.toFile())
            } else null
         } catch (e: Exception) { null }
      }

   suspend fun fetchNoticeToMarinersGraphic(graphic: NoticeToMarinersGraphic): Uri? =
      withContext(Dispatchers.IO) {
         try {
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

               getUriForFile(application, "${application.packageName}.fileprovider", file)
            } else null
         } catch (e: Exception) { null }
      }

   suspend fun getNoticeToMarinersCorrections(
      locationFilter: Filter?,
      noticeFilter: Filter?
   ): List<ChartCorrection> = withContext(Dispatchers.IO) {
      val corrections = mutableListOf<ChartCorrection>()

      val values = locationFilter?.value.toString().split(",")
      val latitude = values.getOrNull(0)?.toDoubleOrNull()
      val longitude = values.getOrNull(1)?.toDoubleOrNull()

      if (latitude != null && longitude != null) {
         val nauticalMiles = values.getOrNull(2).toString().toDouble()
         val nauticalMilesMeasurement = nauticalMiles * METERS_IN_NAUTICAL_MILE

         val metersPoint = GeometryUtils.degreesToMeters(longitude, latitude)
         val southWest = GeometryUtils.metersToDegrees(metersPoint.x - nauticalMilesMeasurement, metersPoint.y - nauticalMilesMeasurement)
         val northEast = GeometryUtils.metersToDegrees(metersPoint.x + nauticalMilesMeasurement, metersPoint.y + nauticalMilesMeasurement)

         val response = service.getNoticeToMarinersCorrections(
            minLatitude = southWest.y,
            minLongitude = southWest.x,
            maxLatitude = northEast.y,
            maxLongitude = northEast.x,
            noticeNumber = noticeFilter?.value?.toString()?.toIntOrNull()
         )

         if (response.isSuccessful) {
            response.body()?.let { corrections.addAll(it.chartCorrections) }
         }
      }

      corrections
   }

   companion object {
      private const val METERS_IN_NAUTICAL_MILE = 1852
   }
}