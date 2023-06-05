package mil.nga.msi.ui.light.detail

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightSector
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightAction
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.light.LightViewModel
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun LightDetailScreen(
   key: LightKey,
   close: () -> Unit,
   onAction: (LightAction) -> Unit,
   viewModel: LightViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val lights by viewModel.getLight(key.volumeNumber, key.featureNumber).observeAsState(emptyList())
   Column {
      TopBar(
         title = LightRoute.Detail.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      LightDetailContent(
         lights = lights,
         baseMap = baseMap,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(LightAction.Zoom(it)) },
         onShare = { onAction(LightAction.Share(it.toString())) },
         onCopyLocation = { onAction(LightAction.Location(it)) }
      )
   }
}

@Composable
private fun LightDetailContent(
   lights: List<Light>,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: (NavPoint) -> Unit,
   onShare: (Light) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (lights.isNotEmpty()) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            LightHeader(lights.first(), baseMap, tileProvider, onZoom, onShare, onCopyLocation)
            LightCharacteristics(lights.drop(0))
         }
      }
   }
}

@Composable
private fun LightHeader(
   light: Light,
   baseMap: BaseMapType?,
   lightTileProvider: TileProvider,
   onZoom: (NavPoint) -> Unit,
   onShare: (Light) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card {
      Column {
         light.name?.let { name ->
            Surface(
               color = DataSource.LIGHT.color,
               contentColor = MaterialTheme.colorScheme.onPrimary,
               modifier = Modifier.fillMaxWidth()
            ) {
               Text(
                  text = name.trim(),
                  style = MaterialTheme.typography.headlineSmall,
                  modifier = Modifier.padding(16.dp)
               )
            }
         }

         MapClip(
            latLng = LatLng(light.latitude, light.longitude),
            baseMap = baseMap,
            tileProvider = lightTileProvider
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "${light.featureNumber} ${light.internationalFeature ?: ""} ${light.volumeNumber}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = light.sectionHeader,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 4.dp)
               )

               light.structure?.let { structure ->
                  Text(
                     text = structure,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }

               val height = listOfNotNull(light.heightFeet, light.heightMeters)
               if (height.isNotEmpty()) {
                  Text(
                     text = "Focal Plane Elevation: ${height.first()}ft ${if (height.size > 1) "${height.last()}m" else ""}",
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 8.dp)
                  )
               }
            }

            LightFooter(
               light,
               onZoom = { onZoom(NavPoint(light.latitude, light.longitude))},
               onShare = { onShare(light) },
               onCopyLocation)
         }
      }
   }
}

@Composable
private fun LightFooter(
   light: Light,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      LightLocation(light.dms, onCopyLocation)
      LightActions(onZoom, onShare)
   }
}

@Composable
private fun LightLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun LightActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}


@Composable
private fun LightCharacteristics(
   lights: List<Light>
) {
   if (lights.isNotEmpty()) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
         Text(
            text = "CHARACTERISTICS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
         )
      }
   }

   lights.forEach { light ->
      if (light.isRacon()) {
         RaconDetail(light)
      } else {
         LightDetail(light)
      }
   }
}

@Composable
private fun RaconDetail(
   light: Light
) {
   Card(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Column(
         Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
      ) {
         light.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
         }

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
               .fillMaxWidth()
               .padding(bottom = 8.dp)
         ) {
            light.morseCode()?.let { morseCode ->
               Column {
                  Text(
                     text = "Signal",
                     style = MaterialTheme.typography.bodyLarge,
                     modifier = Modifier.padding(bottom = 8.dp)
                  )

                  MorseCode(
                     text = morseCode,
                     modifier = Modifier.padding(bottom = 8.dp)
                  )

                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = light.characteristic?.trim() ?: "",
                        style = MaterialTheme.typography.bodyMedium
                     )
                  }
               }
            }

            light.remarks?.let { remarks ->
               Column {
                  Text(
                     text = "Remarks",
                     style = MaterialTheme.typography.bodyLarge
                  )

                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = remarks,
                        style = MaterialTheme.typography.bodyMedium
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun MorseCode(
   text: String,
   modifier: Modifier = Modifier,
) {
   Row(modifier = modifier) {
      text.split(" ").forEach { letter ->
         if (letter == "-" || letter == "•") {
            Box(
               modifier = Modifier
                  .padding(end = 8.dp)
                  .height(5.dp)
                  .width(if (letter == "-") 24.dp else 8.dp)
                  .background(MaterialTheme.colorScheme.onSurface)
            )
         }
      }
   }
}

@Composable
private fun LightDetail(
   light: Light
) {
   Card(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Column(
         Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
      ) {

         Row(
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 16.dp)
         ) {
            Column(Modifier.weight(1f)) {
               light.name?.let { name ->
                  Text(
                     text = name,
                     style = MaterialTheme.typography.titleLarge,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.padding(bottom = 8.dp)
                  )
               }

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  light.expandedCharacteristic()?.let { characteristic ->
                     Text(
                        text = characteristic,
                        style = MaterialTheme.typography.bodyMedium
                     )
                  }
               }
            }

            val lightSectors = light.lightSectors
            if (lightSectors.isNotEmpty()) {
               LightImage(
                  lightSectors,
                  arcWidth = 3.0
               )
            }
         }

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
               .fillMaxWidth()
               .padding(bottom = 8.dp)
         ) {
            Column {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Range (nm)",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }

               Text(
                  text = light.range ?: "",
                  style = MaterialTheme.typography.bodyLarge
               )
            }

            light.remarks?.trim()?.let { remarks ->
               Column {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = "Remarks",
                        style = MaterialTheme.typography.bodyMedium
                     )
                  }

                  Text(
                     text = remarks,
                     style = MaterialTheme.typography.bodyLarge
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun LightImage(
   sectors: List<LightSector>,
   arcWidth: Double? = null
) {
   val strokeWidth = 6
   val sizeInPx = with(LocalDensity.current) { 100.dp.toPx() }
   val textSizeInPx = with(LocalDensity.current) { 12.dp.toPx() }

   Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
         .width(100.dp)
         .height(100.dp)
   ) {
      if (arcWidth != null) {
         Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
               color = Color.LightGray,
               radius = sizeInPx / 2,
               style = Stroke(strokeWidth.toFloat())
            )
         }
      }

      sectors.forEach { sector ->
         val startAngle = sector.startDegrees + 90
         val endAngle = sector.endDegrees + 90
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else {
            (360 - startAngle.toFloat()) + endAngle.toFloat()
         }

         Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
               sector.color,
               startAngle.toFloat(),
               sweepAngle,
               topLeft = Offset(0f, 0f),
               useCenter = true,
               size = Size(sizeInPx, sizeInPx)
            )
         }


         sector.text?.let { text ->
            Canvas(modifier = Modifier.fillMaxSize()) {
               drawIntoCanvas {
                  val midPointAngle = (sector.startDegrees) + (sector.endDegrees - sector.startDegrees) / 2.0

                  val paint = Paint().apply {
                     isAntiAlias = true
                     textSize = textSizeInPx
                     color = android.graphics.Color.BLACK
                     typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                  }

                  it.translate(
                     dx = (sizeInPx / 2 - paint.measureText(sector.text) / 2),
                     dy = (sizeInPx + (paint.descent() + paint.ascent()) / 2)
                  )

                  it.nativeCanvas.rotate(
                     midPointAngle.toFloat(),
                     paint.measureText(sector.text) / 2,
                     -(sizeInPx / 2 - paint.measureText(sector.text) / 2)
                  )

                  it.nativeCanvas.drawText(text, 0f, 0f, paint)
               }
            }
         }
      }
   }
}