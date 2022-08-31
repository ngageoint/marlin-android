package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.asam.AsamService
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AsamRemoteDataSource @Inject constructor(
   private val service: AsamService,
   private val localDataSource: AsamLocalDataSource
) {
   suspend fun fetchAsams(): List<Asam> {
      val asams = mutableListOf<Asam>()

      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      val minDate = localDataSource.getLatestAsam()?.date?.let {
         dateFormat.format(it)
      }
      val maxDate = dateFormat.format(Date())

      val response = service.getAsams(minDate = minDate, maxDate = maxDate)
      if (response.isSuccessful) {
         response.body()?.let {
            asams.addAll(it)
         }
      }

      return asams
   }
}