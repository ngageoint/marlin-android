package mil.nga.msi.ui.map.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType

@Composable
fun MapSettingsScreen(
   onClose: () -> Unit,
   viewModel: MapSettingsViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()

   Column {
      TopBar(
         title = "Map Settings",
         buttonIcon = Icons.Default.Close,
         onButtonClicked = { onClose() }
      )

      baseMap?.let {
         MapLayers(it) {
            viewModel.setBaseLayer(it)
         }
      }
   }
}

@Composable
private fun MapLayers(
   baseLayer: BaseMapType,
   onLayerSelected: (BaseMapType) -> Unit
) {
   var openDialog by remember { mutableStateOf(false)  }

   Column(Modifier.padding(horizontal = 32.dp)) {
      Text(
         text = "Map",
         color = MaterialTheme.colors.secondary,
         style= MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
      )
   }

   Row(
      Modifier
         .fillMaxWidth()
         .clickable { openDialog = true }
         .padding(horizontal = 32.dp, vertical = 16.dp)
   ) {
      Column {
         Text(
            text = "Base Map",
            style = MaterialTheme.typography.body1
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = baseLayer.title,
               style = MaterialTheme.typography.body2
            )
         }
      }
   }

   if (openDialog) {
      MapLayerDialog(baseLayer) {
         onLayerSelected(it)
         openDialog = false
      }
   }
}

@Composable
fun MapLayerDialog(
   baseLayer: BaseMapType,
   onLayerSelected: (BaseMapType) -> Unit
) {
   Dialog(onDismissRequest = { onLayerSelected(baseLayer) }) {
      Surface(
         shape = RoundedCornerShape(4.dp)
      ) {
         Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Text(
               text = "Base Map",
               style = MaterialTheme.typography.h6,
               modifier = Modifier
                  .height(64.dp)
                  .wrapContentHeight(align = Alignment.CenterVertically)
            )

            BaseMapType.values().forEach { mapType ->
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  RadioButton(
                     selected = mapType == baseLayer,
                     onClick = {
                        onLayerSelected(mapType)
                     }
                  )
                  Text(
                     text = mapType.title,
                     modifier = Modifier.fillMaxWidth()
                  )
               }
            }
         }
      }
   }
}