package mil.nga.msi.ui.geopackage.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.geopackage.GeoPackageFeatureSummary
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
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.GEOPACKAGE,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      GeoPackageFeatureSummary(
         name = name,
         table = table
      )

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}