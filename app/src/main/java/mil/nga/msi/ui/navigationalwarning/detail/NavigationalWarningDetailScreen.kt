package mil.nga.msi.ui.navigationalwarning.detail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import mil.nga.msi.R
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapShape
import mil.nga.msi.ui.navigationalwarning.MapAnnotations
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningAction
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningState
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NavigationalWarningDetailScreen(
   key: NavigationalWarningKey,
   close: () -> Unit,
   onAction: (NavigationalWarningAction) -> Unit,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   val state by viewModel.getNavigationalWarning(key).observeAsState()
   val baseMap by viewModel.baseMap.observeAsState()

   Column {
      TopBar(
         title = "Navigational Warning",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      NavigationalWarningDetailContent(
         baseMap = baseMap,
         state = state,
         onShare = { onAction(NavigationalWarningAction.Share(state?.warning.toString())) },
         onZoom = { bounds ->
            bounds?.let {
               onAction(NavigationalWarningAction.Zoom(bounds))
            }
         }
      )
   }
}

@Composable
private fun NavigationalWarningDetailContent(
   state: NavigationalWarningState?,
   baseMap: BaseMapType?,
   onShare: () -> Unit,
   onZoom: (LatLngBounds?) -> Unit
) {
   if (state != null) {
      val mapBounds = state.warning.bounds()

      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            Card {
               NavigationalWarningHeader(
                  baseMap = baseMap,
                  mapBounds = mapBounds,
                  state = state
               )
               NavigationalWarningFooter(
                  onShare = onShare,
                  onZoom = if (state.warning.geoJson != null) {
                     { onZoom(mapBounds) }
                  } else null
               )
            }
            NavigationalWarningText(state.warning.text)
         }
      }
   }
}

@Composable
fun NavigationalWarningHeader(
   state: NavigationalWarningState,
   showMap: Boolean = true,
   mapBounds: LatLngBounds? = null,
   baseMap: BaseMapType? = null
) {
   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

   Column {
      if (showMap && state.annotations.isNotEmpty()) {
         Map(
            baseMap = baseMap,
            mapBounds = mapBounds,
            annotations = state.annotations
         )
      }

      Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = dateFormat.format(state.warning.issueDate),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         val identifier = "${state.warning.number}/${state.warning.year}"
         val subregions = state.warning.subregions?.joinToString(",")?.let { "($it)" }
         val header = listOfNotNull(state.warning.navigationArea.title, identifier, subregions).joinToString(" ")
         Text(
            text = header,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )

         NavigationalWarningProperty(title = "Status", value = state.warning.status)
         NavigationalWarningProperty(title = "Authority", value = state.warning.authority)
         state.warning.cancelDate?.let { date ->
            NavigationalWarningProperty(title = "Cancel Date", value = dateFormat.format(date))
         }
      }
   }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun Map(
   baseMap: BaseMapType?,
   mapBounds: LatLngBounds?,
   annotations: List<MapShape>
) {
   val cameraPositionState = rememberCameraPositionState()

   LaunchedEffect(mapBounds) {
      mapBounds?.let { bounds ->
         cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, 20),
            durationMs = 1000
         )
      }
   }

   val uiSettings = MapUiSettings(
      zoomControlsEnabled = false,
      zoomGesturesEnabled = false,
      compassEnabled = false
   )

   val mapStyleOptions = if (isSystemInDarkTheme()) {
      MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.map_theme_night)
   } else null

   val properties = baseMap?.let {
      MapProperties(
         mapType = it.asMapType(),
         mapStyleOptions = mapStyleOptions
      )
   } ?: MapProperties()

   GoogleMap(
      cameraPositionState = cameraPositionState,
      properties = properties,
      uiSettings = uiSettings,
      modifier = Modifier
         .fillMaxWidth()
         .height(200.dp)
   ) {
      MapAnnotations(
         annotations = annotations
      )
   }
}

@Composable
private fun NavigationalWarningFooter(
   onShare: () -> Unit,
   onZoom: (() -> Unit)? = null
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier
         .fillMaxWidth()
         .padding(vertical = 8.dp, horizontal = 16.dp)
   ) {
      NavigationalWarningActions(onShare, onZoom)
   }
}

@Composable
private fun NavigationalWarningActions(
   onShare: () -> Unit,
   onZoom: (() -> Unit)? = null
) {
   IconButton(onClick = { onShare() }) {
      Icon(Icons.Default.Share,
         tint = MaterialTheme.colorScheme.tertiary,
         contentDescription = "Share Navigational Warning"
      )
   }

   onZoom?.let { onClick ->
      IconButton(onClick = onClick) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to Navigational Warning"
         )
      }
   }
}

@Composable
private fun NavigationalWarningText(
   text: String?
) {
   Column(Modifier.padding(vertical = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "WARNING",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
         )
      }

      Card(
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            text?.let {
               SelectionContainer {
                  Text(
                     text = it,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(all = 16.dp)
                  )
               }
            }
         }
      }
   }
}

@Composable
fun NavigationalWarningProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(Modifier.padding(vertical = 8.dp)) {
         Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = value,
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}