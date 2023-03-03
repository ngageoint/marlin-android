package mil.nga.msi.ui.map.overlay

import android.net.Uri

class OsmTileProvider(
   width: Int = 256,
   height: Int = 256,
) : GridTileProvider(BASE_URI, width, height) {

   companion object {
      private val BASE_URI = Uri.parse("https://osm.gs.mil/tiles/default")
   }
}