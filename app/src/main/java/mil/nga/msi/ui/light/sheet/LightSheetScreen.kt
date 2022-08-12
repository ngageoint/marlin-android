package mil.nga.msi.ui.light.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightViewModel
@Composable
fun LightSheetScreen(
   key: LightKey,
   onDetails: (() -> Unit)? = null,
   viewModel: LightViewModel = hiltViewModel()
) {
   val light by viewModel.getLight(key.volumeNumber, key.featureNumber).observeAsState()
   light?.first()?.let {
      LightContent(light = it) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun LightContent(
   light: Light,
   onDetails: () -> Unit,
) {
   Column(
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxHeight()
         .padding(vertical = 8.dp, horizontal = 16.dp)
   ) {
      Column(Modifier.weight(1f)) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "${light.featureNumber} ${light.internationalFeature ?: ""} ${light.volumeNumber}",
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.overline,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         light.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = light.sectionHeader,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )

            light.structure?.let { structure ->
               Text(
                  text = structure,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(top = 4.dp)
               )
            }
         }
      }

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}