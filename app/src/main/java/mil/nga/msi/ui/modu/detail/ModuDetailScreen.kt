package mil.nga.msi.ui.modu.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.modu.ModuAction
import mil.nga.msi.ui.modu.ModuViewModel
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModuDetailScreen(
   name: String,
   close: () -> Unit,
   onAction: (ModuAction) -> Unit,
   viewModel: ModuViewModel = hiltViewModel()
) {
   val modu by viewModel.getModu(name).observeAsState()
   val baseMap by viewModel.baseMap.observeAsState()

   Column {
      TopBar(
         title = "MODU",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      ModuDetailContent(
         modu = modu,
         baseMap = baseMap,
         tileProvider = viewModel.tileProvider,
         onZoom = { modu?.let { onAction(ModuAction.Zoom(Point(it.latitude, it.latitude))) } },
         onShare = { onAction(ModuAction.Share(modu.toString())) },
         onCopyLocation = { onAction(ModuAction.Location(it)) }
      )
   }
}

@Composable
private fun ModuDetailContent(
   modu: Modu?,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   if (modu != null) {
      Surface(
         color = MaterialTheme.colorScheme.screenBackground,
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            ModuHeader(modu, baseMap, tileProvider, onZoom, onShare, onCopyLocation)
            ModuInformation(modu)
         }
      }
   }
}

@Composable
private fun ModuHeader(
   modu: Modu,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   Card(
      modifier = Modifier.padding(bottom = 16.dp)
   ) {
      Column {
         MapClip(
            latLng = LatLng(modu.latitude, modu.longitude),
            tileProvider = tileProvider,
            baseMap = baseMap
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               modu.date.let { date ->
                  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                  Text(
                     text = dateFormat.format(date),
                     fontWeight = FontWeight.SemiBold,
                     style = MaterialTheme.typography.labelSmall,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                  )
               }
            }

            Text(
               text = modu.name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )

            ModuFooter(modu, onZoom, onShare, onCopyLocation)
         }
      }
   }
}

@Composable
private fun ModuFooter(
   modu: Modu,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      ModuLocation(modu.dms, onCopyLocation)
      ModuActions(onZoom, onShare)
   }
}

@Composable
private fun ModuLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit,
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun ModuActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Share MODU"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Zoom to MODU"
         )
      }
   }
}

@Composable
private fun ModuInformation(
   modu: Modu
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.titleMedium
      )
   }

   Card(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
      ) {
         ModuProperty(title = "Rig Status", value = modu.rigStatus.toString())
         ModuProperty(title = "Special Status", value = modu.specialStatus)
         ModuProperty(title = "Distance", value = modu.distance?.toString())
         ModuProperty(title = "Position", value = modu.position)
         ModuProperty(title = "Navigation Area", value = modu.navigationArea)
         ModuProperty(title = "Region", value = modu.region)
         ModuProperty(title = "Subregion", value = modu.subregion)
      }
   }
}

@Composable
private fun ModuProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(Modifier.padding(vertical = 8.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = title,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = value,
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}