package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.geometry.Bounds
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.location.webMercatorToWgs84
import mil.nga.msi.ui.location.wgs84ToWebMercator
import mil.nga.sf.Point
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Polygon
import java.io.ByteArrayOutputStream
import kotlin.math.*

interface TileRepository {
   suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage>
}

interface DataSourceImage {
   val feature: Feature
   val dataSource: DataSource

   fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap>

   fun pointImage(
      context: Context,
      mapZoom: Int,
   ): Bitmap {
      val scale = context.resources.displayMetrics.density * 2.5
      val size = ((mapZoom) * scale).toInt()

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val circleSize = size / 2f
      canvas.drawCircle(
         circleSize,
         circleSize,
         circleSize / 2,
         Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = dataSource.color.toArgb()
         }
      )

      if (mapZoom > 6) {
         val iconSize = (circleSize * .6).toInt()
         val icon = AppCompatResources.getDrawable(context, dataSource.icon)!!
         icon.setBounds(0, 0, iconSize, iconSize)
         canvas.drawBitmap(
            icon.toBitmap(),
            null,
            RectF(
               ((circleSize / 2) + (circleSize - iconSize) / 2),
               ((circleSize / 2) + (circleSize - iconSize) / 2),
               (circleSize + (circleSize / 2) - (circleSize - iconSize) / 2),
               (circleSize + (circleSize / 2) - (circleSize - iconSize) / 2)
            ),
            null
         )
      }

      return bitmap
   }

   fun circleImage(
      context: Context,
      mapZoom: Int,
      radius: Double
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 10).toInt()
      val stroke = (context.resources.displayMetrics.density * 1)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = dataSource.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = dataSource.color.copy(alpha = .2f).toArgb()
            style = Paint.Style.FILL
            strokeWidth = stroke
         }
      )

      return bitmap
   }

   fun lineImage(
      context: Context,
      lineString: LineString,
      mapZoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): Bitmap {
      val stroke = (context.resources.displayMetrics.density * 2)
      val bitmap = Bitmap.createBitmap(tileSize.toInt(), tileSize.toInt(), Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val path = Path()
      val firstPoint = lineString.lineString.points.first()
      val firstPixel = toPixel(LatLng(firstPoint.y, firstPoint.x), tileBounds, tileSize)
      path.moveTo(firstPixel.x.toFloat(), firstPixel.y.toFloat())

      lineString.lineString.points.drop(1).forEach { point ->
         val pixel = toPixel(LatLng(point.y, point.x), tileBounds, tileSize)
         path.lineTo(pixel.x.toFloat(), pixel.y.toFloat())
      }

      val paint = Paint().apply {
         isAntiAlias = true
         style = Paint.Style.STROKE
         strokeWidth = stroke
         color = dataSource.color.toArgb()
      }

      canvas.drawPath(path, paint)

      return bitmap
   }

   fun polygonImage(
      context: Context,
      polygon: Polygon,
      mapZoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): Bitmap {
      val stroke = (context.resources.displayMetrics.density * 2)
      val bitmap = Bitmap.createBitmap(tileSize.toInt(), tileSize.toInt(), Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val path = Path()
      val lineString = polygon.polygon.exteriorRing
      val firstPoint = lineString.points.first()
      val firstPixel = toPixel(LatLng(firstPoint.y, firstPoint.x), tileBounds, tileSize)
      path.moveTo(firstPixel.x.toFloat(), firstPixel.y.toFloat())

      lineString.points.drop(1).forEach { point ->
         val pixel = toPixel(LatLng(point.y, point.x), tileBounds, tileSize)
         path.lineTo(pixel.x.toFloat(), pixel.y.toFloat())
      }

      canvas.drawPath(
         path,
         Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = stroke
            color = dataSource.color.toArgb()
         }
      )

      canvas.drawPath(
         path,
         Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = stroke
            color = dataSource.color.copy(alpha = .3f).toArgb()
         }
      )

      return bitmap
   }

   private fun toPixel(latLng: LatLng, tileBounds3857: Bounds, tileSize: Double): Point {
      val object3857Location = to3857(latLng)
      val xPosition = (((object3857Location.x - tileBounds3857.minX) / (tileBounds3857.maxX - tileBounds3857.minX)) * tileSize)
      val yPosition = tileSize - (((object3857Location.y - tileBounds3857.minY) / (tileBounds3857.maxY - tileBounds3857.minY)) * tileSize)
      return Point(xPosition, yPosition)
   }

   private fun to3857(latLng: LatLng): Point {
      val a = 6378137.0
      val lambda = latLng.longitude / 180 * Math.PI
      val phi = latLng.latitude / 180 * Math.PI
      val x = a * lambda
      val y = a * ln(tan(Math.PI / 4 + phi / 2))

      return Point(x, y)
   }
}

open class DataSourceTileProvider(
   private val application: Application,
   private val repository: TileRepository
) : TileProvider {
   override fun getTile(x: Int, y: Int, z: Int): Tile {
      if (z < 3) return TileProvider.NO_TILE

      val width = (application.resources.displayMetrics.density * 256).toInt()
      val height = (application.resources.displayMetrics.density * 256).toInt()

      val minTileLon = longitude(x = x, zoom = z)
      val maxTileLon = longitude(x = x + 1, zoom = z)
      val minTileLat = latitude(y = y + 1, zoom = z)
      val maxTileLat = latitude(y = y, zoom = z)

      val neCorner3857 = Point(maxTileLon, maxTileLat).wgs84ToWebMercator()
      val swCorner3857 = Point(minTileLon, minTileLat).wgs84ToWebMercator()
      val minTileX = swCorner3857.x
      val minTileY = swCorner3857.y
      val maxTileX = neCorner3857.x
      val maxTileY = neCorner3857.y

      // Border tile by 40 miles, biggest light in MSI.
      // Border has to be at least 256 pixels as well
      val tolerance = max(40.0 * 1852, ((maxTileX - minTileX) / (width / 2)) * 40)

      val neCornerTolerance = Point(maxTileX + tolerance, maxTileY + tolerance).webMercatorToWgs84()
      val swCornerTolerance = Point(minTileX - tolerance, minTileY - tolerance).webMercatorToWgs84()
      val minQueryLon = swCornerTolerance.x
      val maxQueryLon = neCornerTolerance.x
      val minQueryLat = swCornerTolerance.y
      val maxQueryLat = neCornerTolerance.y

      val tileBounds = Bounds(
         swCorner3857.x,
         neCorner3857.x,
         swCorner3857.y,
         neCorner3857.y
      )

      val items = runBlocking {
         repository.getTileableItems(
            minLatitude = minQueryLat,
            maxLatitude = maxQueryLat,
            minLongitude = minQueryLon,
            maxLongitude = maxQueryLon
         )
      }

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      items.forEach { item ->
         item.image(application, z, tileBounds, width.toDouble()).forEach { image ->
            val translate = !(image.height == height && image.width == width)
            val destination = if (translate) {
               val centroid = item.feature.geometry.geometry.centroid
               val webMercator = Point(centroid.x, centroid.y).wgs84ToWebMercator()
               val xPosition = (((webMercator.x - minTileX) / (maxTileX - minTileX)) * width)
               val yPosition = height - (((webMercator.y - minTileY) / (maxTileY - minTileY)) * height)
               Rect(
                  xPosition.toInt() - image.width,
                  yPosition.toInt() - image.height,
                  xPosition.toInt() + image.width,
                  yPosition.toInt() + image.height
               )
            } else Rect(0, 0, width, height)

            canvas.drawBitmap(image, null, destination, null)
         }
      }

      val output = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
      return Tile(width, height, output.toByteArray())
   }

   private fun longitude(x: Int, zoom: Int): Double {
      return x.toDouble() / 2.0.pow(zoom.toDouble()) * 360.0 - 180.0
   }

   private fun latitude(y: Int, zoom: Int): Double {
      val n = PI - 2.0 * PI * y / 2.0.pow(zoom.toDouble())
      return 180.0 / PI * atan(0.5 * (exp(n) - exp(-n)))
   }
}