package mil.nga.msi.ui.map.overlay

import android.net.Uri
import mil.nga.geopackage.tiles.TileBoundingBoxUtils
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.repository.preferences.Credentials
import java.net.MalformedURLException
import java.net.URL

open class GridTileProvider(
   private val baseUrl: Uri,
   service: LayerService,
   credentials: Credentials? = null,
   width: Int = 256,
   height: Int = 256,
   private val minZoom: Int? = null,
   private val maxZoom: Int? = null,
   private val invertYAxis: Boolean = false
) : MarlinTileProvider(service, credentials, width, height) {

   constructor(service: LayerService, layer: Layer, credentials: Credentials? = null): this(
      service = service,
      baseUrl = Uri.parse(layer.url),
      credentials = credentials,
      minZoom = layer.minZoom,
      maxZoom = layer.maxZoom,
      invertYAxis = layer.type == LayerType.TMS
   )

   override fun getTileUrl(x: Int, y: Int, z: Int): URL? {
      if (!withinZoom(z, minZoom, maxZoom)) return null

      val yAxis = if (invertYAxis) {
         TileBoundingBoxUtils.getYAsOppositeTileFormat(z.toLong(), y)
      } else y

      val url = baseUrl.buildUpon()
         .appendPath("$z")
         .appendPath("$x")
         .appendPath("${yAxis}.png")
         .build()

      return try {
         URL(url.toString())
      } catch (e: MalformedURLException) {
         null
      }
   }
}