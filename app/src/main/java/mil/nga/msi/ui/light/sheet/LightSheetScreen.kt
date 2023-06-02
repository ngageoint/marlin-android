package mil.nga.msi.ui.light.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightViewModel

@Composable
fun LightSheetScreen(
   key: LightKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: LightViewModel = hiltViewModel()
) {
   val light by viewModel.getLight(key.volumeNumber, key.featureNumber).observeAsState()

   Column(modifier = modifier) {
      LightContent(light = light?.first()) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun LightContent(
   light: Light?,
   onDetails: () -> Unit,
) {
   Column(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(48.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = DataSource.LIGHT.color)
         })

         Image(
            painter = painterResource(id = mil.nga.msi.R.drawable.ic_baseline_lightbulb_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "Light icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "${light?.featureNumber.orEmpty()} ${light?.internationalFeature.orEmpty()} ${light?.volumeNumber.orEmpty()}",
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         light?.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            light?.sectionHeader?.let { sectionHeader ->
            Text(
               text = sectionHeader,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 4.dp)
            )
            }

            light?.structure?.let { structure ->
               Text(
                  text = structure,
                  style = MaterialTheme.typography.bodyMedium,
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