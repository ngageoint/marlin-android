package mil.nga.msi.ui.report

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun ReportsScreen(
   close: () -> Unit,
   onTap: (ReportRoute) -> Unit,
) {
   val scrollState = rememberScrollState()
   Column {
      TopBar(
         title = ReportRoute.List.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant,
         modifier = Modifier.fillMaxSize()
      ) {
         Column(
            Modifier
               .fillMaxSize()
               .verticalScroll(scrollState)
               .background(MaterialTheme.colorScheme.screenBackground)
               .padding(bottom = 16.dp)
         ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "Submit Reports to NGA",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
               )
            }

            Reports(ReportRoute.ASAM) {
               onTap(ReportRoute.ASAM)
            }

            Divider()

            Reports(ReportRoute.Observer) {
               onTap(ReportRoute.Observer)
            }

            Divider()

            Reports(ReportRoute.MODU) {
               onTap(ReportRoute.MODU)
            }

            Divider()

            Reports(ReportRoute.PortVisit) {
               onTap(ReportRoute.PortVisit)
            }

            Divider()

            Reports(ReportRoute.HostileShip) {
               onTap(ReportRoute.HostileShip)
            }
         }
      }
   }
}

@Composable
private fun Reports(
   route: ReportRoute,
   onTap: () -> Unit,
) {
   Column {
      Report(route) {
         onTap()
      }
   }
}

@Composable
private fun Report(
   route: ReportRoute,
   onTap: () -> Unit,
) {
   val bitmap = AppCompatResources.getDrawable(LocalContext.current, route.icon)!!.toBitmap().asImageBitmap()

   Surface {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
      ) {

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               bitmap = bitmap,
               modifier = Modifier.padding(start = 8.dp),
               contentDescription = "Report Icon"
            )
         }

         Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier
                  .height(56.dp)
                  .fillMaxWidth()
                  .padding(horizontal = 8.dp)
            ) {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                  Text(
                     text = route.title,
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.Medium
                  )
               }
            }
         }
      }
   }
}