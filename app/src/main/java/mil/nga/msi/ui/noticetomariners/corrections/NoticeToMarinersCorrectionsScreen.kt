package mil.nga.msi.ui.noticetomariners.corrections

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import java.time.Year

@Composable
fun NoticeToMarinersCorrectionsScreen(
   onNoticeTap: (Int) -> Unit,
   close: () -> Unit,
   viewModel: NoticeToMarinersCorrectionsViewModel = hiltViewModel()
) {
   val loading by viewModel.loading.observeAsState(true)
   val corrections by viewModel.corrections.observeAsState(emptyMap())

   Column(
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      TopBar(
         title = NoticeToMarinersRoute.Home.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(Modifier.fillMaxHeight()) {
         Box {
            if (loading) {
               Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                     .fillMaxSize()
                     .padding(horizontal = 16.dp)
               ) {
                  Column(
                     horizontalAlignment = Alignment.CenterHorizontally,
                  ) {
                     CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Text(
                           text = "Loading Chart Corrections...",
                           style = MaterialTheme.typography.headlineSmall,
                           modifier = Modifier.padding(bottom = 16.dp)
                        )
                     }

                     LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()
                     )
                  }

               }
            } else {
               ChartCorrections(
                  corrections = corrections,
                  onNoticeTap = { onNoticeTap(it.noticeNumber) }
               )
            }
         }
      }
   }
}

@Composable
private fun ChartCorrections(
   corrections: Map<String, List<ChartCorrection>>,
   onNoticeTap: (ChartCorrection) -> Unit
) {
   val scrollState = rememberScrollState()
   var expanded by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

   Column(
      modifier = Modifier.verticalScroll(scrollState)
   ) {
      corrections.forEach { entry ->
         val chartNumber = entry.value.first().chartNumber
         Chart(
            correction = entry.value.first(),
            notices = entry.value,
            expand = expanded[chartNumber] ?: false,
            onExpand = { expand ->
               expanded = expanded.toMutableMap().apply {
                  put(chartNumber, expand)
               }
            },
            onNoticeTap = { onNoticeTap(it) }
         )
      }
   }
}

@Composable
private fun Chart(
   correction: ChartCorrection,
   notices: List<ChartCorrection>,
   expand: Boolean,
   onExpand: (Boolean) -> Unit,
   onNoticeTap: (ChartCorrection) -> Unit
) {
   val angle: Float by animateFloatAsState(
      targetValue = if (expand) 180F else 0F,
      animationSpec = tween(
         durationMillis = 250,
         easing = FastOutSlowInEasing
      ),
      label = "angle_animator"
   )

   Card(
      modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
   ) {
      Column {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
         ) {
            Column(
               modifier = Modifier.weight(1f)
            ) {
               Row(
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(horizontal = 16.dp)
                     .padding(bottom = 8.dp)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                     Text(
                        text = "Chart No. ${correction.chartNumber}",
                        style = MaterialTheme.typography.titleMedium
                     )
                  }

                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = "${correction.editionNumber} Ed. ${correction.editionDate}",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleSmall
                     )
                  }
               }

               Row(
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(start = 16.dp, end = 8.dp)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = "Current Notice: ${correction.currentNoticeNumber}",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleSmall
                     )
                  }

                  if (correction.noticeDecade >= 99 && correction.noticeWeek >= 29 || correction.noticeDecade <= Year.now().value % 1000) {
                     TextButton(
                        onClick = { onNoticeTap(correction) },
                        colors = ButtonDefaults.textButtonColors(
                           contentColor = MaterialTheme.colorScheme.tertiary
                        )
                     ) {
                        Text("NTM ${correction.currentNoticeNumber} Details")
                     }
                  }
               }
            }

            IconButton(
               onClick = { onExpand(!expand) }
            ) {
               Icon(
                  imageVector = Icons.Default.ExpandMore,
                  tint = MaterialTheme.colorScheme.tertiary,
                  modifier = Modifier.rotate(angle),
                  contentDescription = "Expand Filter"
               )
            }
         }

         Column(
            Modifier
               .fillMaxWidth()
               .animateContentSize()
         ) {
            if (expand) {
               Notices(notices) {
                  onNoticeTap(it)
               }
            }
         }
      }
   }
}

@Composable
private fun Notices(
   notices: List<ChartCorrection>,
   onNoticeTap: (ChartCorrection) -> Unit
) {
   notices.forEach { notice ->
      Column(
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Divider(Modifier.padding(bottom = 16.dp))

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = "Notice: ${notice.currentNoticeNumber}",
               style = MaterialTheme.typography.titleMedium,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         notice.corrections.forEach { correction ->
            Column {
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(vertical = 16.dp)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = "${correction.action}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                     )

                     Text(
                        text = "${correction.text}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                     )
                  }
               }
            }
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "${notice.authority}",
               style = MaterialTheme.typography.titleSmall,
               modifier = Modifier.padding(bottom = 8.dp)
            )
         }

         Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
         ) {
            if (notice.noticeDecade >= 99 && notice.noticeWeek >= 29 || notice.noticeDecade <= Year.now().value % 1000) {
               TextButton(
                  onClick = { onNoticeTap(notice) },
                  colors = ButtonDefaults.textButtonColors(
                     contentColor = MaterialTheme.colorScheme.tertiary
                  )
               ) {
                  Text("NTM ${notice.currentNoticeNumber} Details")
               }
            }
         }
      }
   }
}