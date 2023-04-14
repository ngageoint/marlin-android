package mil.nga.msi.ui.map.overlay

import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import kotlinx.coroutines.runBlocking
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.repository.preferences.Credentials
import java.net.URL

abstract class MarlinTileProvider(
   private val service: LayerService,
   private val credentials: Credentials? = null,
   protected val width: Int = 256,
   protected val height: Int = 256
): TileProvider {

   override fun getTile(x: Int, y: Int, z: Int): Tile? {
      val url = getTileUrl(x, y, z)

      val credentials = this.credentials?.let {
         okhttp3.Credentials.basic(it.username, it.password)
      }

      val response = runBlocking {
         service.getTile(url.toString(), credentials)
      }

      return response.body()?.bytes()?.let {
         Tile(width, height, it)
      }
   }

   abstract fun getTileUrl(x: Int, y: Int, z: Int): URL?

   fun withinZoom(
      zoom: Int,
      minZoom: Int?,
      maxZoom: Int?
   ): Boolean {
      val withinMin = minZoom == null || zoom >= minZoom
      val withinMax = maxZoom == null || zoom <= maxZoom
      return withinMin && withinMax
   }
}