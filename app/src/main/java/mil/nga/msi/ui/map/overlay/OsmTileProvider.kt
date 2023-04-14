package mil.nga.msi.ui.map.overlay

import android.net.Uri
import mil.nga.msi.network.layer.LayerService

class OsmTileProvider(
   service: LayerService,
   width: Int = 256,
   height: Int = 256,
) : GridTileProvider(
   service = service,
   baseUrl = BASE_URI,
   width = width,
   height = height
) {

   companion object {
      private val BASE_URI = Uri.parse("https://osm.gs.mil/tiles/default")
   }
}