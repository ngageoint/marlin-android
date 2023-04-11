package mil.nga.msi.ui.geopackage.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.ui.geopackage.GeoPackageViewModel

@Composable
fun GeoPackageFeatureSheetScreen(
   key: GeoPackageFeatureKey,
   onDetails: () -> Unit,
   modifier: Modifier = Modifier,
   viewModel: GeoPackageViewModel = hiltViewModel()
) {
   val layer by viewModel.layer.observeAsState()
   viewModel.setLayer(key.layerId)

   Column(modifier = modifier) {
      FeatureContent(
         name = layer?.name ?: "",
         table = key.table
      ) {
         onDetails()
      }
   }
}

@Composable
private fun FeatureContent(
   name: String,
   table: String,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(48.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = DataSource.GEOPACKAGE.color)
         })

         Image(
            painter = painterResource(id = R.drawable.ic_round_place_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "GeoPackage Feature Icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = name,
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         Text(
            text = table,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
         )
      }

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}