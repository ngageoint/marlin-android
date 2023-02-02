package mil.nga.msi.ui.noticetomariners.detail

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.screenBackground
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Composable
fun NoticeToMarinersDetailScreen(
   noticeNumber: Int?,
   close: () -> Unit,
   onGraphicTap: (NoticeToMarinersGraphic) -> Unit,
   viewModel: NoticeToMarinersDetailViewModel = hiltViewModel()
) {
   val loading by viewModel.loading.observeAsState(true)
   val noticeToMariners by viewModel.noticeToMariners.observeAsState()

   LaunchedEffect(noticeNumber) {
      noticeNumber?.let { viewModel.setNoticeNumber(it) }
   }

   Column(modifier = Modifier) {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Box {
         if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
         } else {
            NoticeToMariners(noticeToMariners) { onGraphicTap(it) }
         }
      }
   }
}

@Composable
private fun NoticeToMariners(
   noticeToMariners: NoticeToMarinersState?,
   onGraphicTap: (NoticeToMarinersGraphic) -> Unit
) {
   val scrollState = rememberScrollState()

   Surface(
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {
      Column(
         Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = "CHARTLETS",
               style = MaterialTheme.typography.subtitle1,
               modifier = Modifier.padding(top = 8.dp)
            )
         }

         NoticeToMarinersCharts(graphics = noticeToMariners?.graphics ?: emptyList()) {
            onGraphicTap(it)
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = "FILES",
               style = MaterialTheme.typography.subtitle1,
               modifier = Modifier.padding(top = 8.dp)
            )
         }

         NoticeToMarinersFiles(noticeToMariners)
      }
   }
}

@Composable
private fun NoticeToMarinersCharts(
   graphics: List<NoticeToMarinersGraphics>,
   onTap: (NoticeToMarinersGraphic) -> Unit
) {
   graphics.sortedBy { it.graphicType }.windowed(3, 3, true).forEach { window ->
      Column(Modifier.fillMaxWidth()) {
         Row {
            Box(Modifier.weight(.33f)) {
               window.getOrNull(0)?.let { graphic ->
                  NoticeToMarinersChart(graphic) { onTap(it) }
               }
            }
            Box(Modifier.weight(.33f)) {
               window.getOrNull(1)?.let { graphic ->
                  NoticeToMarinersChart(graphic) { onTap(it) }
               }
            }
            Box(Modifier.weight(.33f)) {
               window.getOrNull(2)?.let { graphic ->
                  NoticeToMarinersChart(graphic) { onTap(it) }
               }
            }
         }
      }
   }
}

@Composable
private fun NoticeToMarinersChart(
   graphics: NoticeToMarinersGraphics,
   onTap: (NoticeToMarinersGraphic) -> Unit
) {
   val graphic = NoticeToMarinersGraphic.fromNoticeToMarinersGraphics(graphics)

   Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
         .fillMaxWidth()
         .height(200.dp)
         .clickable { onTap(graphic) }
   ) {
      Box(modifier = Modifier.fillMaxWidth()) {
         SubcomposeAsyncImage(
            model = graphic.url,
            loading = { CircularProgressIndicator(Modifier.padding(16.dp)) },
            modifier = Modifier.padding(horizontal = 12.dp),
            contentScale = ContentScale.Fit,
            contentDescription = "Notice to Mariner Graphic"
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
         Text(
            text = graphic.title,
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.SemiBold
         )
      }
   }
}

@Composable
private fun NoticeToMarinersFiles(
   state: NoticeToMarinersState?
) {
   val dateFormatter = DateTimeFormatter
      .ofLocalizedDate(FormatStyle.FULL)
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.systemDefault())

   Column {
      state?.notices?.forEach { notice ->
         Card(
            elevation = 4.dp,
            modifier = Modifier.padding(vertical = 8.dp)
         ) {
            Column(
               Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp)
            ) {
               Text(
                  text = "${notice.title} ${if (notice.isFullPublication == true) notice.fileExtension else ""}",
                  style = MaterialTheme.typography.subtitle1,
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
               )

               notice.fileSize?.toLong()?.let { fileSize ->
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                     Text(
                        text = "File Size: ${Formatter.formatFileSize(LocalContext.current, fileSize)}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                     )
                  }
               }

               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                  Text(
                     text = "Upload Time: ${dateFormatter.format(notice.uploadTime)}",
                     style = MaterialTheme.typography.body2,
                     fontWeight = FontWeight.SemiBold,
                     modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                  )
               }
            }
         }
      }
   }
}