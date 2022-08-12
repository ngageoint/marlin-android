package mil.nga.msi.repository.light

import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightDao
import javax.inject.Inject

class LightLocalDataSource @Inject constructor(
   private val dao: LightDao
) {
//   fun observeAsams() = dao.observeAsams()
   fun observeMapItems() = dao.observeMapItems()
   fun observeLightListItems() = dao.getLightListItems()

   fun observeLight(
      volumeNumber: String,
      featureNumber: String
   ) = dao.observeLight(volumeNumber, featureNumber)

   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ) = dao.getLight(volumeNumber, featureNumber, characteristicNumber)

   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude)

   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
      characteristicNumber: Int
   ) = dao.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber)

   suspend fun getLights(): List<Light> = dao.getLights()
   suspend fun getLatestLight(volumeNumber: String) = dao.getLatestLight(volumeNumber)

   suspend fun insert(lights: List<Light>) = dao.insert(lights)
}