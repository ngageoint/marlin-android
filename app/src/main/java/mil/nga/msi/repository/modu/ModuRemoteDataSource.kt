package mil.nga.msi.repository.modu

import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.network.modu.ModuService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ModuRemoteDataSource @Inject constructor(
   private val service: ModuService,
   private val localDataSource: ModuLocalDataSource
) {
   suspend fun fetchModus(): List<Modu> {
      val modus = mutableListOf<Modu>()

      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      val minDate = localDataSource.getLatestModu()?.date?.let {
         dateFormat.format(it)
      }
      val maxDate = dateFormat.format(Date())

      val response = service.getModus(minDate = minDate, maxDate = maxDate)
      if (response.isSuccessful) {
         response.body()?.let {
            modus.addAll(it.modus)
         }
      }

      return modus
   }
}