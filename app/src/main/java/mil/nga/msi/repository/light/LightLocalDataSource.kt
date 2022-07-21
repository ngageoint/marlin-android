package mil.nga.msi.repository.light

import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightDao
import javax.inject.Inject

class LightLocalDataSource @Inject constructor(
   private val dao: LightDao
) {
//   fun observeAsams() = dao.observeAsams()
//   fun observeAsam(reference: String) = dao.observeAsam(reference)
//   fun observeAsamMapItems() = dao.observeAsamMapItems()
//   fun observeAsamListItems() = dao.getAsamListItems()
//
//   suspend fun getAsam(reference: String) = dao.getAsam(reference)
   suspend fun getLights(): List<Light> = dao.getLights()
   suspend fun getLatestLight(volumeNumber: String) = dao.getLatestLight(volumeNumber)

   suspend fun insert(lights: List<Light>) = dao.insert(lights)
}