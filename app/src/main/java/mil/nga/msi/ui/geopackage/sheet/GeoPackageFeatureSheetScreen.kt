package mil.nga.msi.ui.geopackage.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.geopackage.GeoPackageFeature
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.geopackage.GeoPackageFeatureSummary
import mil.nga.msi.ui.geopackage.GeoPackageViewModel

@Composable
fun GeoPackageFeatureSheetScreen(
   key: GeoPackageFeatureKey,
   onDetails: () -> Unit,
   modifier: Modifier = Modifier,
   onBookmark: (GeoPackageFeatureKey, Bookmark?) -> Unit,
   viewModel: GeoPackageViewModel = hiltViewModel()
) {
   val feature by viewModel.feature.observeAsState()
   LaunchedEffect(key) {
      viewModel.setGeoPackageFeatureKey(key)
   }

   Column(modifier = modifier) {
      FeatureContent(
         feature = feature,
         onDetails = onDetails,
         onBookmark = {
            onBookmark(key, feature?.bookmark)
         }
      )
   }
}

@Composable
private fun FeatureContent(
   feature: GeoPackageFeature?,
   onDetails: () -> Unit,
   onBookmark: () -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.GEOPACKAGE,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      feature?.let {
         GeoPackageFeatureSummary(
            name = it.name,
            table = it.table,
            bookmark = feature.bookmark
         )
      }

      Row(horizontalArrangement = Arrangement.SpaceBetween) {
         TextButton(
            onClick = onDetails
         ) {
            Text("MORE DETAILS")
         }

         DataSourceActions(
            bookmarked = feature?.bookmark != null,
            onBookmark = onBookmark,
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}