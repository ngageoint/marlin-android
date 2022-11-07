package mil.nga.msi.repository.light

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightDao
import mil.nga.msi.datasource.light.LightListItem
import javax.inject.Inject

class LightLocalDataSource @Inject constructor(
   private val dao: LightDao
) {
   fun observeLightMapItems() = dao.observeLightMapItems()
   fun observeLightListItems(query: SimpleSQLiteQuery): PagingSource<Int, LightListItem> = dao.observeLightListItems(query)

   fun getLights(query: SimpleSQLiteQuery) = dao.getLights(query)

   fun isEmpty() = dao.count() == 0
   suspend fun existingLights(ids: List<String>) = dao.existingLights(ids)

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
      maxLongitude: Double,
      characteristicNumber: Int
   ) = dao.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber)

   suspend fun getLights(): List<Light> = dao.getLights()
   suspend fun getLatestLight(volumeNumber: String) = dao.getLatestLight(volumeNumber)

   suspend fun insert(lights: List<Light>) = dao.insert(lights)
}