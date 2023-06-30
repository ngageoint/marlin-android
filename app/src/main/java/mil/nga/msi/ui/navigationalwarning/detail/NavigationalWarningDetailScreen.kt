package mil.nga.msi.ui.navigationalwarning.detail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.maps.android.compose.rememberCameraPositionState
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapShape
import mil.nga.msi.ui.navigationalwarning.MapAnnotations
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningState
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NavigationalWarningDetailScreen(
   key: NavigationalWarningKey,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   viewModel.setWarningKey(key)
   val state by viewModel.warningState.observeAsState()
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
         onShare = { warning ->
            onAction(NavigationalWarningAction.Share(warning))
         },
         onBookmark = { (warning, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromNavigationalWarning(warning)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onZoom = { warning ->
            warning.bounds()?.let {
               onAction(NavigationalWarningAction.Zoom(it))
            }
         }
      )
   }
}

@Composable
private fun NavigationalWarningDetailContent(
   state: NavigationalWarningState?,
   baseMap: BaseMapType?,
   onZoom: (NavigationalWarning) -> Unit,
   onShare: (NavigationalWarning) -> Unit,
   onBookmark: (NavigationalWarningWithBookmark) -> Unit,
) {
   if (state != null) {
      val (warning, bookmark) = state.warningWithBookmark

      val mapBounds = warning.bounds()

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

               DataSourceFooter(
                  bookmarked = bookmark != null,
                  onShare = { onShare(warning) },
                  onBookmark = { onBookmark(state.warningWithBookmark) },
                  onZoom = if (warning.geoJson != null) {
                     { onZoom(warning) }
                  } else null
               )
            }
            NavigationalWarningText(warning.text)
         }
      }
   }
}

@Composable
private fun NavigationalWarningHeader(
   state: NavigationalWarningState,
   showMap: Boolean = true,
   mapBounds: LatLngBounds? = null,
   baseMap: BaseMapType? = null
) {
   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
   val (warningWithBookmark, annotations) = state
   val (warning, bookmark) = warningWithBookmark

   Column {
      Surface(
         color = DataSource.NAVIGATION_WARNING.color,
         contentColor = MaterialTheme.colorScheme.onPrimary,
         modifier = Modifier.fillMaxWidth()
      ) {
         val identifier = "${warning.number}/${warning.year}"
         val subregions = warning.subregions?.joinToString(",")?.let { "($it)" }
         val header = listOfNotNull(warning.navigationArea.title, identifier, subregions).joinToString(" ")
         Text(
            text = header,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
         )
      }

      if (showMap && annotations.isNotEmpty()) {
         Map(
            baseMap = baseMap,
            mapBounds = mapBounds,
            annotations = annotations
         )
      }

      Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = dateFormat.format(warning.issueDate),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         NavigationalWarningProperty(title = "Authority", value = warning.authority)
         warning.cancelDate?.let { date ->
            NavigationalWarningProperty(title = "Cancel Date", value = dateFormat.format(date))
         }
      }

      BookmarkNotes(notes = bookmark?.notes)
   }
}

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