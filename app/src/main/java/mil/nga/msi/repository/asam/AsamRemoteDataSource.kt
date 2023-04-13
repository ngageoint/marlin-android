package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.asam.AsamService
import mil.nga.msi.network.asam.AsamResponse
import javax.inject.Inject

class AsamRemoteDataSource @Inject constructor(
   private val service: AsamService
) {
   suspend fun fetchAsams(): AsamResponse {
      val asams = mutableListOf<Asam>()

      val response = service.getAsams()
      if (response.isSuccessful) {
         response.body()?.let {
            asams.addAll(it.asams)
         }
      }

      return AsamResponse()
   }
}