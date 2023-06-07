package mil.nga.msi.ui.geopackage.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.geopackage.Feature
import mil.nga.msi.ui.geopackage.FeatureAttribute
import mil.nga.msi.ui.geopackage.FeatureProperty
import mil.nga.msi.ui.geopackage.GeoPackageFeatureAction
import mil.nga.msi.ui.geopackage.GeoPackageViewModel
import mil.nga.msi.ui.geopackage.MediaProperty
import mil.nga.msi.ui.coordinate.CoordinateTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.NavPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GeoPackageFeatureDetailScreen(
   key: GeoPackageFeatureKey,
   close: () -> Unit,
   onAction: (GeoPackageFeatureAction) -> Unit,
   viewModel: GeoPackageViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val tileProvider by viewModel.tileProvider.observeAsState()
   val featureState by viewModel.feature.observeAsState()

   LaunchedEffect(key) {
      viewModel.setFeature(key.layerId, key.table, key.featureId)
   }

   Column {
      TopBar(
         title = "GeoPackage",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      FeatureDetailContent(
         feature = featureState,
         baseMap = baseMap,
         tileProvider = tileProvider,
         onAction = onAction
      )
   }
}

@Composable
private fun FeatureDetailContent(
   feature: Feature?,
   baseMap: BaseMapType?,
   tileProvider: TileProvider?,
   onAction: (GeoPackageFeatureAction) -> Unit
) {
   if (feature != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            FeatureHeader(
               feature = feature,
               baseMap = baseMap,
               tileProvider = tileProvider,
               onAction = onAction
            )

            FeatureDetails(
               feature = feature,
               onMedia = onAction
            )
         }
      }
   }
}

@Composable
private fun FeatureHeader(
   feature: Feature,
   baseMap: BaseMapType?,
   tileProvider: TileProvider?,
   onAction: (GeoPackageFeatureAction) -> Unit
) {
   Card(
      modifier = Modifier.padding(bottom = 16.dp)
   ) {
      feature.latLng?.let { latLng ->
         MapClip(
            latLng = latLng,
            tileProvider = tileProvider,
            baseMap = baseMap
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = feature.name,
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         Text(
            text = feature.table,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
         )

         FeatureFooter(
            latLng = feature.latLng,
            onAction = onAction,
         )
      }
   }
}

@Composable
private fun FeatureFooter(
   latLng: LatLng?,
   onAction: (GeoPackageFeatureAction) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      latLng?.let {
         FeatureLocation(
            latLng = latLng,
            onCopyLocation = {
               onAction(GeoPackageFeatureAction.Location(it))
            }
         )
         FeatureActions(
            onZoom = {
               onAction(GeoPackageFeatureAction.Zoom(NavPoint(it.latitude, it.longitude)))
            }
         )
      }
   }
}

@Composable
private fun FeatureLocation(
   latLng: LatLng,
   onCopyLocation: (String) -> Unit,
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun FeatureActions(
   onZoom: () -> Unit
) {
   Row {
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to GeoPackage Feature"
         )
      }
   }
}

@Composable
fun FeatureDetails(
   feature: Feature?,
   onMedia: (GeoPackageFeatureAction.Media) -> Unit
) {
   if (feature != null) {
      Column {
         if (feature.properties.isNotEmpty()) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
              Text(
                  text = "DETAILS",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(8.dp)
               )
            }

            GeoPackageProperties(feature.properties) { property ->
               val key = GeoPackageMediaKey(feature.id, property.mediaTable, property.mediaId)
               onMedia(GeoPackageFeatureAction.Media(key))
            }
         }

         if (feature.attributes.isNotEmpty()) {
            Divider(
               color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
               modifier = Modifier.height(8.dp)
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "ATTRIBUTES",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(8.dp)
               )
            }

            GeoPackageAttributes(feature.attributes) { property ->
               val key = GeoPackageMediaKey(feature.id, property.mediaTable, property.mediaId)
               onMedia(GeoPackageFeatureAction.Media(key))
            }
         }
      }
   }
}

@Composable
fun GeoPackageProperties(
   properties: List<FeatureProperty>,
   onClick: ((MediaProperty) -> Unit)? = null
) {
   Card(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(16.dp)) {
         properties
            .sortedBy { property -> property.key }
            .forEach { property ->
               Column(Modifier.padding(bottom = 16.dp)) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = property.key,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                     )
                  }

                  when {
                     property is MediaProperty -> {
                        GeoPackageMedia(property) {
                           onClick?.invoke(property)
                        }
                     }
                     property.value is Boolean -> {
                        val text = if (property.value.toString().toInt() == 1) "true" else "false"
                        GeoPackageAttributeText(text)
                     }
                     property.value is Date -> {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm zz", Locale.getDefault())
                        val text = (property.value as? Date)?.let { dateFormat.format(it) } ?: property.value.toString()
                        GeoPackageAttributeText(text)
                     }
                     property.value is Number -> {
                        GeoPackageAttributeText(property.value.toString())
                     }
                     else -> {
                        GeoPackageAttributeText(property.value.toString())
                     }
                  }
               }
            }
      }
   }
}

@Composable
private fun GeoPackageAttributes(
   attributes: List<FeatureAttribute>,
   onClick: ((MediaProperty) -> Unit)? = null
) {
   attributes.forEach { attribute ->
      Column {
         GeoPackageProperties(properties = attribute.properties) { onClick?.invoke(it) }
      }
   }
}

@Composable
private fun GeoPackageAttributeText(value: String) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
      Text(
         text = value,
         style = MaterialTheme.typography.titleMedium
      )
   }
}

@Composable
private fun GeoPackageMedia(
   property: MediaProperty,
   onClick: (() -> Unit)? = null
) {
   when {
      property.contentType.contains("image/") -> {
         val bitmap = BitmapFactory.decodeByteArray(property.value, 0, property.value.size)
         GeoPackageImage(bitmap, onClick)
      }
      property.contentType.contains("video/") -> {
         GeoPackageMediaIcon(Icons.Default.PlayArrow, onClick)
      }
      property.contentType.contains("audio/") -> {
         GeoPackageMediaIcon(Icons.Default.VolumeUp, onClick)
      }
      else -> {
         GeoPackageMediaIcon(Icons.Default.AttachFile, onClick)
      }
   }
}

@Composable
private fun GeoPackageImage(
   bitmap: Bitmap,
   onClick: (() -> Unit)? = null
) {
   Box(
      Modifier
         .fillMaxWidth()
         .height(200.dp)
         .clip(MaterialTheme.shapes.medium)
         .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
         .background(Color(0x19FFFFFF))
         .clickable { onClick?.invoke() }
   ) {
      Image(
         bitmap = bitmap.asImageBitmap(),
         contentDescription = "Image from GeoPackage",
         modifier = Modifier.fillMaxSize()
      )
   }
}

@Composable
private fun GeoPackageMediaIcon(
   icon: ImageVector,
   onClick: (() -> Unit)? = null
) {
   Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
         .fillMaxWidth()
         .height(200.dp)
         .clip(MaterialTheme.shapes.medium)
         .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
         .clickable { onClick?.invoke() }
   ) {

      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .height(144.dp)
            .width(144.dp)
            .clip(CircleShape)
            .background(Color(0x54000000))
      ) {
         Icon(
            imageVector = icon,
            contentDescription = "Media Icon",
            tint = Color(0xDEFFFFFF),
            modifier = Modifier
               .height(84.dp)
               .width(84.dp)
         )
      }
   }
}