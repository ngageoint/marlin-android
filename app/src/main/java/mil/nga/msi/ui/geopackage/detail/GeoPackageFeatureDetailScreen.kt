package mil.nga.msi.ui.geopackage.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.geopackage.GeoPackageFeature
import mil.nga.msi.geopackage.GeoPackageFeatureAttribute
import mil.nga.msi.geopackage.GeoPackageFeatureProperty
import mil.nga.msi.geopackage.GeoPackageMediaProperty
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.GeoPackageFeatureAction
import mil.nga.msi.ui.geopackage.GeoPackageViewModel
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.geopackage.GeoPackageFeatureSummary
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GeoPackageFeatureDetailScreen(
   key: GeoPackageFeatureKey,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: GeoPackageViewModel = hiltViewModel()
) {
   val tileProvider by viewModel.tileProvider.observeAsState()
   val featureState by viewModel.feature.observeAsState()

   LaunchedEffect(key) {
      viewModel.setGeoPackageFeatureKey(key)
   }

   Column {
      TopBar(
         title = "GeoPackage",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      FeatureDetailContent(
         feature = featureState,
         tileProvider = tileProvider,
         onZoom = { featureState?.latLng?.let{ onAction(GeoPackageFeatureAction.Zoom(it)) } },
         onBookmark = { feature ->
            if (feature.bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey(key.id(), DataSource.GEOPACKAGE)))
            } else {
               viewModel.deleteBookmark(feature.bookmark)
            }
         },
         onCopyLocation = { onAction(GeoPackageFeatureAction.Location(it)) },
         onMedia = { onAction(GeoPackageFeatureAction.Media) }
      )
   }
}

@Composable
private fun FeatureDetailContent(
   feature: GeoPackageFeature?,
   tileProvider: TileProvider?,
   onZoom: () -> Unit,
   onBookmark: (GeoPackageFeature) -> Unit,
   onCopyLocation: (String) -> Unit,
   onMedia: (GeoPackageMediaKey) -> Unit
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
               tileProvider = tileProvider,
               onZoom = onZoom,
               onBookmark = { onBookmark(feature) },
               onCopyLocation = onCopyLocation
            )

            FeatureDetails(
               feature = feature,
               onMedia = onMedia
            )
         }
      }
   }
}

@Composable
private fun FeatureHeader(
   feature: GeoPackageFeature,
   tileProvider: TileProvider?,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      modifier = Modifier.padding(bottom = 16.dp)
   ) {
      feature.latLng?.let { latLng ->
         MapClip(
            latLng = latLng,
            tileProvider = tileProvider
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         GeoPackageFeatureSummary(
            name = feature.name,
            table = feature.table,
            bookmark = feature.bookmark
         )

         DataSourceActions(
            latLng = feature.latLng,
            bookmarked = feature.bookmark != null,
            onZoom = onZoom,
            onBookmark = onBookmark,
            onCopyLocation = onCopyLocation
         )
      }
   }
}

@Composable
fun FeatureDetails(
   feature: GeoPackageFeature?,
   onMedia: (GeoPackageMediaKey) -> Unit
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

            GeoPackageProperties(feature.properties) { property: GeoPackageMediaProperty ->
               val key = GeoPackageMediaKey(feature.id, property.mediaTable, property.mediaId)
               onMedia(key)
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
               onMedia(key)
            }
         }
      }
   }
}

@Composable
fun GeoPackageProperties(
   properties: List<GeoPackageFeatureProperty>,
   onClick: ((GeoPackageMediaProperty) -> Unit)? = null
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
                     property is GeoPackageMediaProperty -> {
                        GeoPackageMedia(property) {
                           onClick?.invoke(property)
                        }
                     }
                     property.value is Boolean -> {
                        val value = (property.value as? Boolean) == true
                        val text = if (value) "true" else "false"
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
   attributes: List<GeoPackageFeatureAttribute>,
   onClick: ((GeoPackageMediaProperty) -> Unit)? = null
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
   property: GeoPackageMediaProperty,
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