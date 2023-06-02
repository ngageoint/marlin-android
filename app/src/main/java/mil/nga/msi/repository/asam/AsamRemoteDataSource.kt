package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.asam.AsamService
import javax.inject.Inject

class AsamRemoteDataSource @Inject constructor(
   private val service: AsamService
) {
   suspend fun fetchAsams(): List<Asam> {
      val asams = mutableListOf<Asam>()

      val response = service.getAsams()
      if (response.isSuccessful) {
         response.body()?.let {
            asams.addAll(it.asams)
         }
      }

      return asams
   }
}