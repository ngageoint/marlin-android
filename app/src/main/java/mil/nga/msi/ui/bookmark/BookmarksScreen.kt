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
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersWithBookmark
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.geopackage.GeoPackageFeature
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.action.ElectronicPublicationAction
import mil.nga.msi.ui.action.GeoPackageFeatureAction
import mil.nga.msi.ui.action.LightAction
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.action.NoticeToMarinersAction
import mil.nga.msi.ui.action.PortAction
import mil.nga.msi.ui.action.RadioBeaconAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.electronicpublication.ElectronicPublicationSummary
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningSummary
import mil.nga.msi.ui.port.PortSummary
import mil.nga.msi.ui.radiobeacon.RadioBeaconSummary
import mil.nga.msi.ui.geopackage.GeoPackageFeatureSummary

sealed class BookmarkAction {
   data object Tap: BookmarkAction()
   data object Zoom: BookmarkAction()
   data object Share: BookmarkAction()
   data object Bookmark: BookmarkAction()
   data object Location : BookmarkAction()
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
   when (itemWithBookmark.dataSource) {
      DataSource.ASAM -> {
         val asam = AsamWithBookmark(itemWithBookmark.item as Asam, itemWithBookmark.bookmark)
         AsamBookmark(asamWithBookmark = asam, onAction = onAction)
      }
      DataSource.DGPS_STATION -> {
         val dgpsStation = DgpsStationWithBookmark(itemWithBookmark.item as DgpsStation, itemWithBookmark.bookmark)
         DgpsStationBookmark(dgpsStationWithBookmark = dgpsStation, onAction = onAction)
      }
      DataSource.ELECTRONIC_PUBLICATION -> {
         val publicationWithBookmark = ElectronicPublicationWithBookmark(itemWithBookmark.item as ElectronicPublication, itemWithBookmark.bookmark)
         ElectronicPublicationBookmark(electronicPublicationWithBookmark = publicationWithBookmark, onAction = onAction)
      }
      DataSource.GEOPACKAGE -> {
         val pair = itemWithBookmark.item as Pair<*, *>
         val key = pair.first as GeoPackageFeatureKey
         val feature = pair.second as GeoPackageFeature
         val featureWithBookmark = feature.copy(bookmark = itemWithBookmark.bookmark)
         GeoPackageFeatureBookmark(
            feature = featureWithBookmark,
            onTap = { onAction(GeoPackageFeatureAction.Tap(key)) },
            onZoom = { onAction(GeoPackageFeatureAction.Zoom(it)) },
            onBookmark = { onAction(Action.Bookmark(BookmarkKey(key.id(), DataSource.GEOPACKAGE))) },
            onCopyLocation = { onAction(GeoPackageFeatureAction.Location(it)) }
         )
      }
      DataSource.LIGHT -> {
         val light = LightWithBookmark(itemWithBookmark.item as Light, itemWithBookmark.bookmark)
         LightBookmark(lightWithBookmark = light, onAction = onAction)
      }
      DataSource.MODU -> {
         val modu = ModuWithBookmark(itemWithBookmark.item as Modu, itemWithBookmark.bookmark)
         ModuBookmark(moduWithBookmark = modu, onAction = onAction)
      }
      DataSource.NAVIGATION_WARNING -> {
         val warningWithBookmark = NavigationalWarningWithBookmark(itemWithBookmark.item as NavigationalWarning, itemWithBookmark.bookmark)
         NavigationalWarningBookmark(warningWithBookmark = warningWithBookmark, onAction = onAction)
      }
      DataSource.NOTICE_TO_MARINERS -> {
         val noticeWithBookmark = NoticeToMarinersWithBookmark(itemWithBookmark.item as Int, itemWithBookmark.bookmark)
         NoticeToMarinersBookmark(noticeWithBookmark = noticeWithBookmark, onAction = onAction)
      }
      DataSource.PORT -> {
         val portWithBookmark = PortWithBookmark(itemWithBookmark.item as Port, itemWithBookmark.bookmark)
         PortBookmark(portWithBookmark = portWithBookmark, onAction = onAction)
      }
      DataSource.RADIO_BEACON -> {
         val beaconWithBookmark = RadioBeaconWithBookmark(itemWithBookmark.item as RadioBeacon, itemWithBookmark.bookmark)
         RadioBeaconBookmark(beaconWithBookmark = beaconWithBookmark, onAction = onAction)
      }
      else -> { /* Datasource cannot be bookmarked */ }
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
      onTap = { onAction(AsamAction.Tap(asam)) },
      onZoom = { onAction(AsamAction.Zoom(asam.latLng)) },
      onShare = { onAction(AsamAction.Share(asam)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromAsam(asam))) },
      onCopyLocation = { onAction(AsamAction.Location(it)) }
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
      onTap = { onAction(DgpsStationAction.Tap(dgpsStation)) },
      onZoom = { onAction(DgpsStationAction.Zoom(dgpsStation.latLng)) },
      onShare = { onAction(DgpsStationAction.Share(dgpsStation)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation))) },
      onCopyLocation = { onAction(DgpsStationAction.Location(it)) }
   ) {
      DgpsStationSummary(dgpsStationWithBookmark)
   }
}

@Composable fun ElectronicPublicationBookmark(
   electronicPublicationWithBookmark: ElectronicPublicationWithBookmark,
   onAction: (Action) -> Unit
) {
   val (publication, bookmark) = electronicPublicationWithBookmark

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.ELECTRONIC_PUBLICATION,
      onTap = { onAction(ElectronicPublicationAction.Tap(publication)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey(publication.s3Key, DataSource.ELECTRONIC_PUBLICATION))) }
   ) {
      ElectronicPublicationSummary(
         publicationWithBookmark = electronicPublicationWithBookmark
      )
   }
}


@Composable fun GeoPackageFeatureBookmark(
   feature: GeoPackageFeature,
   onTap: () -> Unit,
   onZoom: (LatLng) -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   BookmarkCard(
      bookmarked = feature.bookmark != null,
      dataSource = DataSource.GEOPACKAGE,
      location = feature.latLng,
      onTap = onTap,
      onZoom = if (feature.latLng !=  null) {
         { onZoom(feature.latLng) }
      } else {
        null
      },
      onBookmark = onBookmark,
      onCopyLocation = onCopyLocation
   ) {
      GeoPackageFeatureSummary(
         name = feature.name,
         table = feature.table,
         bookmark = feature.bookmark
      )
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
      onTap = { onAction(LightAction.Tap(light)) },
      onZoom = { onAction(LightAction.Zoom(light.latLng)) },
      onShare = { onAction(LightAction.Share(light)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromLight(light))) },
      onCopyLocation = { onAction(LightAction.Location(it)) }
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
      onTap = { onAction(ModuAction.Tap(modu)) },
      onZoom = { onAction(ModuAction.Zoom(modu)) },
      onShare = { onAction(ModuAction.Share(modu)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromModu(modu))) },
      onCopyLocation = { onAction(ModuAction.Location(it)) }
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
      onTap = {
         val key = NavigationalWarningKey.fromNavigationWarning(warning)
         onAction(NavigationalWarningAction.Tap(key))
      },
      onZoom = {
         warning.bounds()?.let {
            onAction(NavigationalWarningAction.Zoom(it))
         }
      },
      onShare = { onAction(NavigationalWarningAction.Share(warning)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromNavigationalWarning(warning))) },
      onCopyLocation = { onAction(NavigationalWarningAction.Location(it)) }
   ) {
      NavigationalWarningSummary(navigationWarningWithBookmark = warningWithBookmark)
   }
}

@Composable fun NoticeToMarinersBookmark(
   noticeWithBookmark: NoticeToMarinersWithBookmark,
   onAction: (Action) -> Unit
) {
   val (noticeNumber, bookmark) = noticeWithBookmark
   val (start, end) = NoticeToMariners.span(noticeNumber)

   BookmarkCard(
      bookmarked = bookmark != null,
      dataSource = DataSource.NOTICE_TO_MARINERS,
      onTap = { onAction(NoticeToMarinersAction.Tap(noticeNumber)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromNoticeToMariners(noticeNumber))) },
   ) {
      Column {
         Text(
            text = noticeNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "$start - $end",
               style = MaterialTheme.typography.titleSmall
            )
         }

         BookmarkNotes(
            notes = bookmark?.notes,
            modifier = Modifier.padding(top = 16.dp)
         )
      }
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
      onTap = { onAction(PortAction.Tap(port)) },
      onZoom = { onAction(PortAction.Zoom(port.latLng)) },
      onShare = { onAction(PortAction.Share(port)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromPort(port))) },
      onCopyLocation = { onAction(PortAction.Location(it)) }
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
      onTap = { onAction(RadioBeaconAction.Tap(beacon)) },
      onZoom = { onAction(RadioBeaconAction.Zoom(beacon.latLng)) },
      onShare = { onAction(RadioBeaconAction.Share(beacon)) },
      onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromRadioBeacon(beacon))) },
      onCopyLocation = { onAction(RadioBeaconAction.Location(it)) }
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
   onTap: () -> Unit,
   onZoom: (() -> Unit)? = null,
   onShare: (() -> Unit)? = null,
   onBookmark: (() -> Unit)? = null,
   onCopyLocation: ((String) -> Unit)? = null,
   summary: @Composable ColumnScope.() -> Unit
) {

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = dataSource,
            modifier = Modifier.padding(vertical = 8.dp)
         )

         summary()

         DataSourceActions(
            latLng = location,
            bookmarked = bookmarked,
            onZoom = onZoom,
            onShare = onShare,
            onBookmark = onBookmark,
            onCopyLocation = onCopyLocation
         )
      }
   }
}