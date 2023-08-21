package mil.nga.msi.ui.modu.sheet

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
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.modu.ModuViewModel

@Composable
fun ModuSheetScreen(
   name: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: ModuViewModel = hiltViewModel()
) {
   viewModel.setName(name)
   val moduWithBookmark by viewModel.moduWithBookmark.observeAsState()
   Column(modifier = modifier) {
      ModuContent(moduWithBookmark) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun ModuContent(
   moduWithBookmark: ModuWithBookmark?,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      DataSourceIcon(dataSource = DataSource.MODU)

      moduWithBookmark?.let { ModuSummary(moduWithBookmark = it) }

      TextButton(
         onClick = { onDetails() },
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Text("MORE DETAILS")
      }
   }
}