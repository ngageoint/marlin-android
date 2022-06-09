package mil.nga.msi.ui.modu.detail

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
import com.google.maps.android.compose.*
import mil.nga.msi.R
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.modu.ModuViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModuDetailScreen(
   name: String,
   close: () -> Unit,
   viewModel: ModuViewModel = hiltViewModel()
) {
   val modu by viewModel.getModu(name).observeAsState()
   val baseMap by viewModel.baseMap.observeAsState()

   Column {
      TopBar(
         title = "MODU",
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      ModuDetailContent(modu, baseMap)
   }
}

@Composable
private fun ModuDetailContent(
   modu: Modu?,
   baseMap: BaseMapType?
) {
   if (modu != null) {
      Column(
         Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
      ) {
         ModuHeader(modu, baseMap)
         ModuInformation(modu)
      }
   }
}

@Composable
private fun ModuHeader(
   modu: Modu,
   baseMap: BaseMapType?
) {
   Card(
      elevation = 4.dp,
      modifier = Modifier.padding(bottom = 16.dp)
   ) {
      Column {
         MapClip(
            latLng = LatLng(modu.latitude, modu.longitude),
            icon = R.drawable.modu_map_marker_24dp,
            baseMap = baseMap
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               modu.date.let { date ->
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

            Text(
               text = modu.name,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )

            ModuFooter(modu)
         }
      }
   }
}

@Composable
private fun ModuFooter(modu: Modu) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      ModuLocation(modu.dms)
      ModuActions()
   }
}

@Composable
private fun ModuLocation(dms: DMS) {
   TextButton(onClick = { /*TODO*/ }) {
      Text(text = dms.format())
   }
}

@Composable
private fun ModuActions() {
   Row {
      IconButton(onClick = {  }) {
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
private fun ModuInformation(
   modu: Modu
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
         val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
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