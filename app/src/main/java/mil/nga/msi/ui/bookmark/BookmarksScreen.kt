package mil.nga.msi.ui.bookmark

import android.location.Location
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.action.LightAction
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.action.PortAction
import mil.nga.msi.ui.action.RadioBeaconAction
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningSummary
import mil.nga.msi.ui.port.PortSummary
import mil.nga.msi.ui.radiobeacon.RadioBeaconSummary

sealed class BookmarkAction() {
   object Tap: BookmarkAction()
   object Zoom: BookmarkAction()
   object Share: BookmarkAction()
   object Bookmark: BookmarkAction()
   class Location(val text: String): BookmarkAction()
}

@Composable
fun BookmarksScreen(
   openDrawer: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: BookmarksViewModel = hiltViewModel()
) {
   val bookmarks by viewModel.bookmarks.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = BookmarkRoute.List.title,
         navigationIcon = Icons.Default.Menu,
         onNavigationClicked = { openDrawer() }
      )

      if (bookmarks.isEmpty()) {
         EmptyState()
      } else {
         Bookmarks(
            bookmarks = bookmarks,
            onAction = { action ->
               if (action is Action.Bookmark) {
                  viewModel.deleteBookmark(action.key)
               } else onAction(action)
            }
         )
      }
   }
}

@Composable
private fun EmptyState() {
   Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
   ) {
      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               Icons.Outlined.Bookmarks,
               modifier = Modifier
                  .size(220.dp)
                  .padding(bottom = 32.dp),
               contentDescription = "Bookmark icon"
            )

            Text(
               text = "No Bookmarks",
               style = MaterialTheme.typography.headlineMedium,
               modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
               text = "Bookmark an item and it will show up here.",
               style = MaterialTheme.typography.titleLarge,
               modifier = Modifier.padding(horizontal = 32.dp),
               textAlign = TextAlign.Center
            )
         }
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Bookmarks(
   bookmarks: List<ItemWithBookmark>,
   onAction: (Action) -> Unit
) {
   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         contentPadding = PaddingValues(vertical = 8.dp),
         modifier = Modifier.padding(8.dp)
      ) {
         items(
            count = bookmarks.count(),
            key = {
               val bookmark = bookmarks[it].bookmark
               "${bookmark.dataSource}-${bookmark.id}"
            }
         ) { index ->
            val bookmark = bookmarks[index]
            Box(Modifier.animateItemPlacement()) {
               Bookmark(
                  itemWithBookmark = bookmark,
                  onAction = onAction
               )
            }
         }
      }
   }
}

@Composable
private fun Bookmark(
   itemWithBookmark: ItemWithBookmark,
   onAction: (Action) -> Unit
) {
   when (itemWithBookmark.item) {
      is Asam -> {
         val asam = AsamWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         AsamBookmark(asamWithBookmark = asam, onAction = onAction)
      }
      is DgpsStation -> {
         val dgpsStation = DgpsStationWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         DgpsStationBookmark(dgpsStationWithBookmark = dgpsStation, onAction = onAction)
      }
      is Light -> {
         val light = LightWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         LightBookmark(lightWithBookmark = light, onAction = onAction)
      }
      is Modu -> {
         val modu = ModuWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         ModuBookmark(moduWithBookmark = modu, onAction = onAction)
      }
      is NavigationalWarning -> {
         val warningWithBookmark = NavigationalWarningWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         NavigationalWarningBookmark(warningWithBookmark = warningWithBookmark, onAction = onAction)
      }
      is Port -> {
         val portWithBookmark = PortWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         PortBookmark(portWithBookmark = portWithBookmark, onAction = onAction)
      }
      is RadioBeacon -> {
         val beaconWithBookmark = RadioBeaconWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         RadioBeaconBookmark(beaconWithBookmark = beaconWithBookmark, onAction = onAction)
      }
   }
}

@Composable fun AsamBookmark(
   asamWithBookmark: AsamWithBookmark,
   onAction: (Action) -> Unit
) {
   val (asam, bookmark) = asamWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.ASAM,
      location = asam.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(AsamAction.Tap(asam))
            BookmarkAction.Zoom -> onAction(AsamAction.Zoom(asam.latLng))
            BookmarkAction.Share -> onAction(AsamAction.Share(asam))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromAsam(asam)))
            is BookmarkAction.Location -> onAction(AsamAction.Location(action.text))
         }
      }
   ) {
      AsamSummary(asamWithBookmark)
   }
}

@Composable fun DgpsStationBookmark(
   dgpsStationWithBookmark: DgpsStationWithBookmark,
   onAction: (Action) -> Unit
) {
   val (dgpsStation, bookmark) = dgpsStationWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.DGPS_STATION,
      location = dgpsStation.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(DgpsStationAction.Tap(dgpsStation))
            BookmarkAction.Zoom -> onAction(DgpsStationAction.Zoom(dgpsStation.latLng))
            BookmarkAction.Share -> onAction(DgpsStationAction.Share(dgpsStation))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation)))
            is BookmarkAction.Location -> onAction(AsamAction.Location(action.text))
         }
      }
   ) {
      DgpsStationSummary(dgpsStationWithBookmark)
   }
}
@Composable fun LightBookmark(
   lightWithBookmark: LightWithBookmark,
   onAction: (Action) -> Unit
) {
   val (light, bookmark) = lightWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.LIGHT,
      location = light.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(LightAction.Tap(light))
            BookmarkAction.Zoom -> onAction(LightAction.Zoom(light.latLng))
            BookmarkAction.Share -> onAction(LightAction.Share(light))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromLight(light)))
            is BookmarkAction.Location -> onAction(LightAction.Location(action.text))
         }
      }
   ) {
      LightSummary(lightWithBookmark = lightWithBookmark)
   }
}

@Composable fun ModuBookmark(
   moduWithBookmark: ModuWithBookmark,
   onAction: (Action) -> Unit
) {
   val (modu, bookmark) = moduWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.MODU,
      location = modu.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(ModuAction.Tap(modu))
            BookmarkAction.Zoom -> onAction(ModuAction.Zoom(modu.latLng))
            BookmarkAction.Share -> onAction(ModuAction.Share(modu))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromModu(modu)))
            is BookmarkAction.Location -> onAction(ModuAction.Location(action.text))
         }
      }
   ) {
      ModuSummary(moduWithBookmark = moduWithBookmark)
   }
}

@Composable fun NavigationalWarningBookmark(
   warningWithBookmark: NavigationalWarningWithBookmark,
   onAction: (Action) -> Unit
) {
   val (warning, bookmark) = warningWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.NAVIGATION_WARNING,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> {
               val key = NavigationalWarningKey.fromNavigationWarning(warning)
               onAction(NavigationalWarningAction.Tap(key))
            }
            BookmarkAction.Zoom -> {
               warning.bounds()?.let {
                  onAction(NavigationalWarningAction.Zoom(it))
               }
            }
            BookmarkAction.Share -> onAction(NavigationalWarningAction.Share(warning))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromNavigationalWarning(warning)))
            is BookmarkAction.Location -> onAction(NavigationalWarningAction.Location(action.text))
         }
      }
   ) {
      NavigationalWarningSummary(navigationWarningWithBookmark = warningWithBookmark)
   }
}

@Composable fun PortBookmark(
   portWithBookmark: PortWithBookmark,
   location: Location? = null,
   onAction: (Action) -> Unit
) {
   val (port, bookmark) = portWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.PORT,
      location = port.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(PortAction.Tap(port))
            BookmarkAction.Zoom -> onAction(PortAction.Zoom(port.latLng))
            BookmarkAction.Share -> onAction(PortAction.Share(port))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromPort(port)))
            is BookmarkAction.Location -> onAction(PortAction.Location(action.text))
         }
      }
   ) {
      PortSummary(
         portWithBookmark = portWithBookmark,
         location = location
      )
   }
}

@Composable fun RadioBeaconBookmark(
   beaconWithBookmark: RadioBeaconWithBookmark,
   onAction: (Action) -> Unit
) {
   val (beacon, bookmark) = beaconWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.RADIO_BEACON,
      location = beacon.latLng,
      onAction = { action ->
         when(action) {
            BookmarkAction.Tap -> onAction(RadioBeaconAction.Tap(beacon))
            BookmarkAction.Zoom -> onAction(RadioBeaconAction.Zoom(beacon.latLng))
            BookmarkAction.Share -> onAction(RadioBeaconAction.Share(beacon))
            BookmarkAction.Bookmark -> onAction(Action.Bookmark(BookmarkKey.fromRadioBeacon(beacon)))
            is BookmarkAction.Location -> onAction(RadioBeaconAction.Location(action.text))
         }
      }
   ) {
      RadioBeaconSummary(
         beaconWithBookmark = beaconWithBookmark
      )
   }
}

@Composable fun BookmarkCard(
   bookmarked: Boolean,
   dataSource: DataSource,
   location: LatLng? = null,
   onAction: (BookmarkAction) -> Unit,
   summary: @Composable ColumnScope.() -> Unit
) {

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(BookmarkAction.Tap) }
   ) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = dataSource,
            modifier = Modifier.padding(vertical = 8.dp)
         )

         summary()

         DataSourceFooter(
            latLng = location,
            bookmarked = bookmarked,
            onZoom = { onAction(BookmarkAction.Zoom) },
            onShare = { onAction(BookmarkAction.Share) },
            onBookmark = { onAction(BookmarkAction.Bookmark) },
            onCopyLocation = { onAction(BookmarkAction.Location(it)) }
         )
      }
   }
}