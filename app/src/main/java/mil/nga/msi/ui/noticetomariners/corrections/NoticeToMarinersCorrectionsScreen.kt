package mil.nga.msi.ui.noticetomariners.corrections

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.screenBackground
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
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                     Text(
                        text = "Loading Chart Corrections...",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(bottom = 16.dp)
                     )
                  }

                  LinearProgressIndicator(Modifier.fillMaxWidth())
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

@Composable
private fun ChartCorrections(
   corrections: Map<String, List<ChartCorrection>>,
   onNoticeTap: (ChartCorrection) -> Unit
) {
   val scrollState = rememberScrollState()
   var expanded by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

   Surface(
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {
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
      )
   )

   Card(
      modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
   ) {
      Column() {
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
                  Text(
                     text = "Chart No. ${correction.chartNumber}",
                     style = MaterialTheme.typography.subtitle1
                  )

                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                     Text(
                        text = "${correction.editionNumber} Ed. ${correction.editionDate}",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.subtitle2
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
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                     Text(
                        text = "Current Notice: ${correction.currentNoticeNumber}",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.subtitle2
                     )
                  }

                  if (correction.noticeDecade >= 99 && correction.noticeWeek >= 29 || correction.noticeDecade <= Year.now().value % 1000) {
                     TextButton(
                        onClick = { onNoticeTap(correction) }
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
                  tint = MaterialTheme.colors.primary,
                  modifier = Modifier.rotate(angle),
                  contentDescription = "Expand Filter"
               )
            }
         }

         Column(
            Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colors.background)
               .animateContentSize()
         ) {
            if (expand) {
               Notices(notices)
            }
         }
      }
   }
}

@Composable
private fun Notices(
   notices: List<ChartCorrection>
) {
   notices.forEach { notice ->
      Column(
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Divider(Modifier.padding(bottom = 16.dp))

         Text(
            text = "Notice: ${notice.currentNoticeNumber}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         notice.corrections.forEach { correction ->
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.padding(vertical = 16.dp)
            ) {
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                  Text(
                     text = "${correction.action}",
                     style = MaterialTheme.typography.subtitle2,
                     modifier = Modifier.weight(1f)
                  )

                  Text(
                     text = "${correction.text}",
                     style = MaterialTheme.typography.subtitle2,
                     modifier = Modifier.weight(1f)
                  )
               }
            }
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = "${notice.authority}",
               style = MaterialTheme.typography.subtitle2,
               modifier = Modifier.padding(bottom = 8.dp)
            )
         }
      }
   }
}