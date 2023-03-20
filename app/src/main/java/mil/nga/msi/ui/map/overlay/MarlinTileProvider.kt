package mil.nga.msi.ui.map.overlay

import com.google.android.gms.maps.model.UrlTileProvider

abstract class MarlinTileProvider(
   width: Int = 256,
   height: Int = 256
): UrlTileProvider(width, height) {
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