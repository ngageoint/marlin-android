package mil.nga.msi.ui.asam.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import mil.nga.msi.R
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamViewModel
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsamDetailScreen(
   reference: String,
   close: () -> Unit,
   onShare: (String) -> Unit,
   onCopyLocation: (String) -> Unit,
   viewModel: AsamViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val asam by viewModel.getAsam(reference).observeAsState()
   Column {
      TopBar(
         title = "ASAM",
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      AsamDetailContent(
         asam = asam,
         baseMap = baseMap,
         onShare = { onShare(asam.toString()) },
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun AsamDetailContent(
   asam: Asam?,
   baseMap: BaseMapType?,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (asam != null) {
      Column(
         Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
      ) {
         AsamHeader(asam, baseMap, onShare, onCopyLocation)
         AsamDescription(asam.description)
         AsamInformation(asam)
      }
   }
}

@Composable
private fun AsamHeader(
   asam: Asam,
   baseMap: BaseMapType?,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(elevation = 4.dp) {
      Column {
         MapClip(
            latLng = LatLng(asam.latitude, asam.longitude),
            icon = R.drawable.asam_map_marker_24dp,
            baseMap = baseMap
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               asam.date.let { date ->
                  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                  Text(
                     text = dateFormat.format(date),
                     fontWeight = FontWeight.SemiBold,
                     style = MaterialTheme.typography.overline,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                  )
               }
            }

            val header = listOfNotNull(asam.hostility, asam.victim).joinToString(": ")
            Text(
               text = header,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )

            AsamFooter(asam, onShare, onCopyLocation)
         }
      }
   }
}

@Composable
private fun AsamFooter(
   asam: Asam,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      AsamLocation(asam.dms, onCopyLocation)
      AsamActions(onShare)
   }
}

@Composable
private fun AsamLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun AsamActions(
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = {  }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}


@Composable
private fun AsamDescription(
   description: String?
) {
   Column(Modifier.padding(vertical = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "DESCRIPTION",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium
         )
      }

      Card(
         elevation = 4.dp,
         modifier = Modifier.padding(vertical = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            description?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.body1,
                  modifier = Modifier.padding(all = 16.dp)
               )
            }
         }
      }
   }
}

@Composable
private fun AsamInformation(
   asam: Asam
) {
   CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.subtitle1,
         fontWeight = FontWeight.Medium
      )
   }

   Card(
      elevation = 4.dp,
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
      ) {
         AsamProperty(title = "Reference Number", value = asam.reference)
         AsamProperty(title = "Position", value = asam.position)
         AsamProperty(title = "Navigation Area", value = asam.navigationArea)
         AsamProperty(title = "Subregion", value = asam.subregion)
         AsamProperty(title = "Hostility", value = asam.hostility)
         AsamProperty(title = "Victim", value = asam.victim)
      }
   }
}

@Composable
private fun AsamProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(Modifier.padding(vertical = 8.dp)) {
         Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = value,
               style = MaterialTheme.typography.body1
            )
         }
      }
   }
}