package mil.nga.msi.ui.electronicpublication

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.ui.main.TopBar
import java.time.Instant

sealed class PublicationAction {
    class Download(val publication: ElectronicPublication): PublicationAction()
    class CancelDownload(val publication: ElectronicPublication): PublicationAction()
    class View(val publication: ElectronicPublication): PublicationAction()
    class Delete(val publication: ElectronicPublication): PublicationAction()
    class Bookmark(val publicationWithBookmark: ElectronicPublicationWithBookmark): PublicationAction()
}

@Composable
fun ElectronicPublicationTypeBrowseScreen(
    onBack: () -> Unit,
    onBookmark: (ElectronicPublication) -> Unit,
    viewModel: ElectronicPublicationTypeBrowseViewModel = hiltViewModel()
) {
    val currentNodeState by viewModel.currentNodeState.collectAsStateWithLifecycle()
    val currentNodeLinks by viewModel.currentNodeLinksState.collectAsStateWithLifecycle()
    val onBackClick = {
        when (currentNodeState.parent) {
            null -> onBack()
            else -> viewModel.backToParent()
        }
    }
    // TODO: how will this work when time zone changes, or when offline?
    val context = LocalContext.current
    BackHandler(onBack = onBackClick)
    ElectronicPublicationTypeBrowseScreen(
        currentNode = currentNodeState,
        currentNodeLinks = currentNodeLinks,
        onLinkClick = { link -> if (link is PublicationFolderLink) viewModel.onFolderLinkClick(link) },
        onBackClick = onBackClick,
        onAction = { action ->
            when (action) {
                is PublicationAction.View -> {
                    val ePubUri = viewModel.publicationShareUri(action.publication)
                    val viewEPub = Intent(Intent.ACTION_VIEW)
                        .setDataAndType(ePubUri, action.publication.downloadMediaType)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(viewEPub)
                }

                is PublicationAction.Download -> viewModel.download(action.publication)
                is PublicationAction.CancelDownload -> viewModel.cancelDownload(action.publication)
                is PublicationAction.Delete -> viewModel.delete(action.publication)
                is PublicationAction.Bookmark -> {
                    if (action.publicationWithBookmark.bookmark == null) {
                        onBookmark(action.publicationWithBookmark.electronicPublication)
                    } else {
                        viewModel.deleteBookmark(action.publicationWithBookmark.bookmark)
                    }
                }
            }
        }
    )
}

@Composable
fun ElectronicPublicationTypeBrowseScreen(
    currentNode: PublicationBrowsingNode,
    currentNodeLinks: PublicationBrowsingLinksArrangement,
    onLinkClick: (PublicationBrowsingLink) -> Unit,
    onBackClick: () -> Unit,
    onAction: (PublicationAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = currentNode.title,
            navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
            onNavigationClicked = onBackClick,
        )
        Surface {
            when (currentNodeLinks) {
                is Publications -> {
                    PublicationList(
                        publicationLinks = currentNodeLinks,
                        onAction = onAction
                    )
                }
                is PublicationSections -> {
                    PublicationSectionsList(
                        sections = currentNodeLinks.sections,
                        onAction = onAction
                    )
                }
                is PublicationFolders -> {
                    PublicationFolderList(
                        folderLinks = currentNodeLinks,
                        onLinkClick = onLinkClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PublicationSectionsList(
    sections: List<PublicationSection>,
    onAction: (PublicationAction) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(state = state) {
        sections.forEach { section ->
            stickyHeader(section.title) {
                Surface {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(),
                            text = section.title,
                        )
                    }
                }
            }
            items(section.publications) { pubLink ->
                PublicationListItem(
                    publicationWithBookmark = pubLink.publicationWithBookmark,
                    onAction = onAction,
                )
                if (pubLink != section.publications.last()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
        item {
            HorizontalDivider()
        }
    }
}

@Composable
fun PublicationListItem(
    publicationWithBookmark: ElectronicPublicationWithBookmark,
    onAction: (PublicationAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        ElectronicPublicationSummary(publicationWithBookmark = publicationWithBookmark)
        ElectronicPublicationFooter(publicationWithBookmark = publicationWithBookmark, onAction = onAction)
    }
}

@Composable
fun PublicationList(
    publicationLinks: Publications,
    onAction: (PublicationAction) -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        LazyColumn {
            publicationLinks.publications.forEachIndexed { index, pubLink ->
                item {
                    PublicationListItem(
                        publicationWithBookmark = pubLink.publicationWithBookmark,
                        onAction = onAction
                    )
                    if (index < publicationLinks.publications.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
            item { HorizontalDivider() }
        }
    }
}

@Composable
fun PublicationFolderList(
    folderLinks: PublicationFolders,
    onLinkClick: (PublicationFolderLink) -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        LazyColumn {
            items(count = folderLinks.folders.size) {
                val folderLink = folderLinks.folders[it]
                ListItem(
                    headlineContent = { Text(folderLink.title) },
                    supportingContent = { Text("${folderLink.fileCount} files") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = folderLink.title
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = folderLink.title
                        )
                    },
                    modifier = Modifier
                        .clickable(
                            onClick = { onLinkClick(folderLink) }
                        )
                        .padding(bottom = 8.dp)
                )
                HorizontalDivider(Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Preview(name = "Publication List Item")
@Composable
fun PreviewPublicationListItem() {
    PublicationListItem(
        publicationWithBookmark = ElectronicPublicationWithBookmark(
            ElectronicPublication(
                s3Key = "pub1",
                sectionDisplayName = "Publication 1",
                fullFilename = "pub1.pdf",
                fileExtension = "pdf",
                fileSize = 13_003_246,
                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z")
            )
        ),
        onAction = {}
    )
}

@Preview(name = "Publication List Item Downloading Percent Progress")
@Composable
fun PreviewPublicationListItemDownloadingPercentProgress() {
    PublicationListItem(
        publicationWithBookmark = ElectronicPublicationWithBookmark(
            ElectronicPublication(
                s3Key = "pub1",
                sectionDisplayName = "Publication 1",
                fullFilename = "pub1.pdf",
                fileExtension = "pdf",
                fileSize = 13_003_246,
                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"),
                isDownloading = true,
                downloadedBytes = 3_456_678,
            )
        ),
        onAction = {}
    )
}

@Preview(name = "Publication List Item Downloading Unknown Progress")
@Composable
fun PreviewPublicationListItemDownloadingUnknownProgress() {
    PublicationListItem(
        publicationWithBookmark = ElectronicPublicationWithBookmark(
            ElectronicPublication(
                s3Key = "pub1",
                sectionDisplayName = "Publication 1",
                fullFilename = "pub1.pdf",
                fileExtension = "pdf",
                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"),
                isDownloading = true,
                downloadedBytes = 3_456_678,
            )
        ),
        onAction = {}
    )
}

@Preview(name = "Publication Sections")
@Composable
fun PreviewPublicationSectionsList() {
    PublicationSectionsList(
        sections = listOf(
            PublicationSection(
                title = "Complete Volumes",
                publications = listOf(
                    PublicationLink(
                        ElectronicPublicationWithBookmark(
                            ElectronicPublication(
                                s3Key = "pub1",
                                sectionDisplayName = "Publication 1",
                                fullFilename = "pub1.pdf",
                                fileSize = 11_224,
                                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z")
                            )
                        )
                    )
                )
            ),
            PublicationSection(
                title = "Single Chapters",
                publications = listOf(
                    PublicationLink(
                        ElectronicPublicationWithBookmark(
                            ElectronicPublication(
                                s3Key = "pub1.1",
                                sectionDisplayName = "Section 1.1",
                                fullFilename = "pub1.1.pdf",
                                fileSize = 23_456,
                                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z")
                            )
                        )
                    ),
                    PublicationLink(
                        ElectronicPublicationWithBookmark(
                            ElectronicPublication(
                                s3Key = "pub1.2",
                                sectionDisplayName = "Section 1.2",
                                fullFilename = "pub1.2.pdf",
                                fileSize = 98_321,
                                uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"), isDownloaded = true)
                        )
                    )
                )
            )
        ),
        onAction = {}
    )
}