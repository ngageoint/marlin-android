package mil.nga.msi.ui.noticetomariners.detail

import android.content.Intent
import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.onSurfaceDisabled
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun NoticeToMarinersDetailScreen(
   noticeNumber: Int?,
   close: () -> Unit,
   onGraphicTap: (NoticeToMarinersGraphic) -> Unit,
   onBookmark: (Int) -> Unit,
   viewModel: NoticeToMarinersDetailViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val context = LocalContext.current
   val loading by viewModel.loading.observeAsState(true)
   val noticeToMariners by viewModel.noticeToMariners.observeAsState()
   var available by remember { mutableStateOf(mapOf<Int, Boolean>()) }
   var downloading by remember { mutableStateOf(mapOf<Int, Boolean>()) }

   LaunchedEffect(noticeNumber) {
      noticeNumber?.let { viewModel.setNoticeNumber(it) }
   }

   LaunchedEffect(noticeToMariners) {
      noticeToMariners?.let {
         available = it.publications.associate { publication ->
            publication.notice.odsEntryId to (publication.uri != null)
         }
      }
   }

   Column(modifier = Modifier) {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(Modifier.fillMaxHeight()) {
         Box {
            if (loading) {
               LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
               NoticeToMariners(
                  noticeToMariners = noticeToMariners,
                  available = available,
                  downloading = downloading,
                  onGraphicTap = { onGraphicTap(it) },
                  onView = { notice ->
                     scope.launch {
                        downloading = downloading.toMutableMap().apply {
                           this[notice.odsEntryId] = true
                        }

                        val uri = viewModel.getNoticeToMarinersPublication(notice)
                        if (uri != null) {
                           available = available.toMutableMap().apply {
                              this[notice.odsEntryId] = true
                           }

                           val shareIntent = Intent.createChooser(Intent().apply {
                              action = Intent.ACTION_SEND
                              type = context.contentResolver.getType(uri)
                              putExtra(Intent.EXTRA_STREAM, uri)
                           }, "Notice to Mariners Publication")

                           context.startActivity(shareIntent)
                        } else {
                           // TODO show download error here
                           downloading = downloading.toMutableMap().apply {
                              this[notice.odsEntryId] = false
                           }
                        }
                     }
                  },
                  onDelete = { notice ->
                     scope.launch {
                        viewModel.deleteNoticeToMarinersPublication(notice)

                        downloading = downloading.toMutableMap().apply {
                           this[notice.odsEntryId] = false
                        }

                        available = available.toMutableMap().apply {
                           this[notice.odsEntryId] = false
                        }
                     }
                  },
                  onBookmark = { state ->
                     if (state.bookmark == null) {
                        onBookmark(state.noticeNumber)
                     } else {
                        viewModel.deleteBookmark(state.bookmark)
                     }
                  }
               )
            }
         }
      }
   }
}

@Composable
private fun NoticeToMariners(
   noticeToMariners: NoticeToMarinersState?,
   available: Map<Int, Boolean>,
   downloading: Map<Int, Boolean>,
   onView: (NoticeToMariners) -> Unit,
   onDelete: (NoticeToMariners) -> Unit,
   onBookmark: (NoticeToMarinersState) -> Unit,
   onGraphicTap: (NoticeToMarinersGraphic) -> Unit
) {
   val scrollState = rememberScrollState()

   Column(
      Modifier
         .fillMaxWidth()
         .padding(horizontal = 8.dp, vertical = 16.dp)
         .verticalScroll(scrollState)
   ) {
      NoticeToMarinersHeader(
         state = noticeToMariners,
         onBookmark = onBookmark
      )

      NoticeToMarinersCharts(graphics = noticeToMariners?.graphics ?: emptyList()) {
         onGraphicTap(it)
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "FILES",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
         )
      }

      NoticeToMarinersPublications(
         state = noticeToMariners,
         available = available,
         downloading = downloading,
         onView = { onView(it) },
         onDelete = { onDelete(it) }
      )
   }
}
@Composable
private fun NoticeToMarinersHeader(
   state: NoticeToMarinersState?,
   onBookmark: (NoticeToMarinersState) -> Unit
) {
   state?.publications?.firstOrNull()?.notice?.let { notice ->
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "NOTICE",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 9.dp)
         )
      }

      Card {
         Column(
            Modifier.padding(16.dp)
         ) {
            Row (Modifier.fillMaxWidth()) {
               Column(
                  Modifier
                     .fillMaxWidth()
                     .weight(1f)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = notice.noticeNumber.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                     )
                  }

                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     val (start, end) = notice.span()
                     Text(
                        text = "$start - $end",
                        style = MaterialTheme.typography.bodySmall
                     )
                  }
               }

               IconButton(onClick = { onBookmark(state) }) {
                  Icon(
                     imageVector = if (state.bookmark != null) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                     tint = MaterialTheme.colorScheme.tertiary,
                     contentDescription = "Bookmark Notice to Mariners"
                  )
               }
            }


            BookmarkNotes(
               notes = state.bookmark?.notes,
               modifier = Modifier.padding(top = 16.dp)
            )
         }
      }
   }
}

@Composable
private fun NoticeToMarinersCharts(
   graphics: List<NoticeToMarinersGraphics>,
   onTap: (NoticeToMarinersGraphic) -> Unit
) {
   val group = graphics.groupBy { it.graphicType }.toSortedMap()
   group.forEach { entry ->
      if (entry.value.isNotEmpty()) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = entry.key.uppercase(Locale.getDefault()),
               style = MaterialTheme.typography.titleMedium,
               modifier = Modifier.padding(vertical = 8.dp)
            )
         }

         Card {
            entry.value.windowed(3, 3, true).forEach { window ->
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

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
         Text(
            text = graphic.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
         )
      }
   }
}

@Composable
private fun NoticeToMarinersPublications(
   state: NoticeToMarinersState?,
   available: Map<Int, Boolean>,
   downloading: Map<Int, Boolean>,
   onView: (NoticeToMariners) -> Unit,
   onDelete: (NoticeToMariners) -> Unit
) {
   val dateFormatter = DateTimeFormatter
      .ofLocalizedDate(FormatStyle.FULL)
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.systemDefault())

   Column {
      state?.publications?.forEach { publication ->
         Card(
            modifier = Modifier.padding(vertical = 8.dp)
         ) {
            Column(
               Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
               val extension = publication.notice.filename.substringAfterLast('.').uppercase()
               Text(
                  text = "${publication.notice.title} ${if (publication.notice.isFullPublication == true) extension else ""}",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.padding(vertical = 4.dp)
               )

               publication.notice.fileSize?.toLong()?.let { fileSize ->
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     Text(
                        text = "File Size: ${Formatter.formatFileSize(LocalContext.current, fileSize)}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                     )
                  }
               }

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
                  Text(
                     text = "Upload Time: ${dateFormatter.format(publication.notice.uploadTime)}",
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.SemiBold,
                     modifier = Modifier.padding(bottom = 8.dp)
                  )
               }

               val isAvailable = available[publication.notice.odsEntryId] == true
               val isDownloading = downloading[publication.notice.odsEntryId] == true
               Row(
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  if (isDownloading && !isAvailable) {
                     LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                           .weight(1f)
                           .padding(end = 16.dp)
                     )
                  }

                  if (isAvailable) {
                     TextButton(
                        onClick = { onDelete(publication.notice) },
                        colors = ButtonDefaults.textButtonColors(
                           contentColor = MaterialTheme.colorScheme.tertiary
                        )
                     ) {
                        Text("Delete")
                     }
                  }

                  TextButton(
                     enabled = !isDownloading || isAvailable,
                     onClick = { onView(publication.notice) },
                     colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                     )
                  ) {
                     Text(if (!isAvailable) "Download" else "View")
                  }
               }
            }
         }
      }
   }
}