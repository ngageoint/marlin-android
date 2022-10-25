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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType

@Composable
fun MapSettingsScreen(
   onLightSettings: () -> Unit,
   onClose: () -> Unit,
   viewModel: MapSettingsViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val gars by viewModel.gars.observeAsState()
   val mgrs by viewModel.mgrs.observeAsState()

   Column {
      TopBar(
         title = "Map Settings",
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      baseMap?.let {
         BaseMapLayer(it) {
            viewModel.setBaseLayer(it)
         }
      }

      GridLayers(
         gars = gars == true,
         mgrs = mgrs == true,
         onGarsToggled = { viewModel.setGARS(it) },
         onMgrsToggled = { viewModel.setMGRS(it) }
      )

      DataSourceSettings(onLightSettings)
   }
}

@Composable
private fun BaseMapLayer(
   baseLayer: BaseMapType,
   onLayerSelected: (BaseMapType) -> Unit
) {
   var openDialog by remember { mutableStateOf(false)  }

   Column(Modifier.padding(horizontal = 32.dp)) {
      Text(
         text = "Map",
         color = MaterialTheme.colors.secondary,
         style= MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(top = 32.dp)
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

@Composable
private fun GridLayers(
   gars: Boolean,
   mgrs: Boolean,
   onGarsToggled: (Boolean) -> Unit,
   onMgrsToggled: (Boolean) -> Unit
) {
   Column(Modifier.padding(horizontal = 32.dp)) {
      Text(
         text = "Grids",
         color = MaterialTheme.colors.secondary,
         style= MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(top = 16.dp)
      )
   }

   Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onGarsToggled(!gars) }
         .padding(horizontal = 32.dp, vertical = 16.dp)
   ) {
      Column {
         Text(
            text = "GARS",
            style = MaterialTheme.typography.body1
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "Global Area Reference System",
               style = MaterialTheme.typography.body2
            )
         }
      }

      Switch(
         checked = gars,
         onCheckedChange = null
      )
   }

   Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onMgrsToggled(!mgrs) }
         .padding(horizontal = 32.dp, vertical = 16.dp)
   ) {
      Column {
         Text(
            text = "MGRS",
            style = MaterialTheme.typography.body1
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "Military Grid Reference System",
               style = MaterialTheme.typography.body2
            )
         }
      }

      Switch(
         checked = mgrs,
         onCheckedChange = null
      )
   }
}

@Composable
private fun DataSourceSettings(
   onLightSettings: () -> Unit

) {
   LightSettings(
      onSettings = onLightSettings
   )
}

@Composable
private fun LightSettings(
   onSettings: () -> Unit
) {
   Column(Modifier.padding(horizontal = 32.dp)) {
      Text(
         text = "DATA SOURCE SETTINGS",
         color = MaterialTheme.colors.secondary,
         style = MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(top = 16.dp)
      )
   }

   Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onSettings() }
         .padding(horizontal = 32.dp, vertical = 16.dp)
   ) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Icon(
            painter = painterResource(id = R.drawable.ic_baseline_lightbulb_24),
            modifier = Modifier.padding(end = 8.dp),
            contentDescription = "Light Icon"
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
         Text(
            text = "Light Settings",
            style = MaterialTheme.typography.body1
         )
      }
   }
}