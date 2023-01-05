@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package mil.nga.msi.ui.electronicpublication

import android.content.Context
import android.text.format.Formatter.formatShortFileSize
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ElectronicPublicationTypeBrowseRoute(
    openDrawer: () -> Unit,
    onBackToRoot: () -> Unit,
    viewModel: ElectronicPublicationTypeBrowseViewModel = hiltViewModel()
) {
    val pubTypeState by viewModel.pubTypeState
    val currentNodeState by viewModel.currentNodeState.collectAsStateWithLifecycle()
    val onBackClick = {
        when (currentNodeState.parent) {
            null -> onBackToRoot()
            else -> viewModel.backToParent()
        }
    }
    // TODO: how will this work when time zone changes, or when offline?
    val formatDateTime = formatDateTimeFunction(LocalContext.current)
    val formatByteCount = formatByteCountFunction(LocalContext.current)
    BackHandler(onBack = onBackClick)
    ElectronicPublicationTypeBrowseScreen(
        pubType = pubTypeState,
        currentNode = currentNodeState,
        onDownloadClick = { /* TODO */ },
        onLinkClick = { link -> if (link is PublicationFolderLink) viewModel.onFolderLinkClick(link) },
        onBackClick = onBackClick,
        formatDateTime = formatDateTime,
        formatByteCount = formatByteCount,
    )
}

@Composable
fun ElectronicPublicationTypeBrowseScreen(
    pubType: ElectronicPublicationType,
    currentNode: PublicationBrowsingNode,
    onDownloadClick: (ElectronicPublication) -> Unit,
    onLinkClick: (PublicationBrowsingLink) -> Unit,
    onBackClick: () -> Unit,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = currentNode.title,
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationClicked = onBackClick,
        )
        Surface(color = MaterialTheme.colors.screenBackground) {
            when (currentNode.links) {
                is Publications -> {
                    PublicationList(
                        publicationLinks = currentNode.links,
                        formatDateTime = formatDateTime,
                        formatByteCount = formatByteCount,
                        onDownloadClick = onDownloadClick
                    )
                }
                is PublicationSections -> {
                    PublicationSectionsList(
                        sections = currentNode.links.sections,
                        formatDateTime = formatDateTime,
                        formatByteCount = formatByteCount,
                        onDownloadClick = onDownloadClick,
                    )
                }
                is PublicationFolders -> {
                    PublicationFolderList(
                        folderLinks = currentNode.links,
                        onLinkClick = onLinkClick
                    )
                }
            }
        }
    }
}

@Composable
fun PublicationSectionsList(
    sections: List<PublicationSection>,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
    onDownloadClick: (pub: ElectronicPublication) -> Unit
) {
    LazyColumn {
        sections.forEach { section ->
            stickyHeader(section.title) {
                Surface {
                    Text(
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier
                            .background(MaterialTheme.colors.screenBackground)
                            .padding(6.dp)
                            .fillMaxWidth(),
                        text = section.title,
                    )
                }
            }
            items(section.publications) { pubLink ->
                PublicationListItem(
                    ePub = pubLink.publication,
                    formatDateTime = formatDateTime,
                    formatByteCount = formatByteCount,
                    onDownloadClick = onDownloadClick,
                )
            }
        }
    }
}

@Composable
fun PublicationListItem(ePub: ElectronicPublication, formatDateTime: (Instant?) -> String?, formatByteCount: (Long?) -> String?, onDownloadClick: (pub: ElectronicPublication) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                style = MaterialTheme.typography.subtitle1,
                text = ePub.sectionDisplayName ?: ""
            )
            Text(
                style = MaterialTheme.typography.caption,
                text = "${ePub.fileExtension?.toUpperCase(Locale.current) ?: "Unknown file type"} - ${formatByteCount(ePub.fileSize) ?: "Unknown size"}"
            )
            Text(
                style = MaterialTheme.typography.caption,
                text = ePub.uploadTime?.let { "Uploaded ${formatDateTime(ePub.uploadTime)}" } ?: "Unknown upload time"
            )
            Row(modifier = Modifier.align(Alignment.End), Arrangement.SpaceBetween) {
                if (ePub.isDownloaded) {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("View", style = MaterialTheme.typography.button)
                    }
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("Delete", style = MaterialTheme.typography.button)
                    }
                }
                else {
                    TextButton(
                        onClick = { onDownloadClick(ePub) }
                    ) {
                        Text("Download", style = MaterialTheme.typography.button)
                    }
                }
            }
        }
    }
}

@Preview(name = "Publication List Item")
@Composable
fun PreviewPublicationListItem() {
    PublicationListItem(
        ePub = ElectronicPublication(
            s3Key = "pub1",
            sectionDisplayName = "Publication 1",
            fullFilename = "pub1.pdf",
            fileExtension = "pdf",
            fileSize = 13_003_246,
            uploadTime = Instant.parse("2022-12-25T12:25:00.0Z")
        ),
        formatDateTime = { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withZone(ZoneId.of("UTC")).format(it) },
        formatByteCount = { String.format("%d", it) },
        onDownloadClick = {}
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
                    PublicationLink(ElectronicPublication(
                        s3Key = "pub1", sectionDisplayName = "Publication 1", fullFilename = "pub1.pdf", fileSize = 11_224, uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"))),
                )
            ),
            PublicationSection(
                title = "Single Chapters",
                publications = listOf(
                    PublicationLink(ElectronicPublication(
                        s3Key = "pub1.1", sectionDisplayName = "Section 1.1", fullFilename = "pub1.1.pdf", fileSize = 23_456, uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"))),
                    PublicationLink(ElectronicPublication(
                        s3Key = "pub1.2", sectionDisplayName = "Section 1.2", fullFilename = "pub1.2.pdf", fileSize = 98_321, uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"), isDownloaded = true)),
                )
            )
        ),
        onDownloadClick = {},
        formatDateTime = { it.toString() ?: "Unknown upload time" },
        formatByteCount = { "${it} bytes" }
    )
}

@Composable
fun PublicationList(
    publicationLinks: Publications,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
    onDownloadClick: (pub: ElectronicPublication) -> Unit
) {
    LazyColumn {
        publicationLinks.publications.forEach { 
            item { 
                PublicationListItem(
                    ePub = it.publication,
                    formatDateTime = formatDateTime,
                    formatByteCount = formatByteCount,
                    onDownloadClick = onDownloadClick,
                )
            }
        }
    }
}

@Composable
fun PublicationFolderList(
    folderLinks: PublicationFolders,
    onLinkClick: (PublicationFolderLink) -> Unit
) {
    Surface {
        LazyColumn {
            items(count = folderLinks.folders.size) {
                val folderLink = folderLinks.folders[it]
                ListItem(
                    text = { Text(folderLink.title) },
                    secondaryText = { Text("${folderLink.fileCount} files") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = folderLink.title
                        )
                    },
                    trailing = {
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
                Divider(startIndent = 16.dp)
            }
        }
    }
}

fun formatByteCountFunction(context: Context): (Long?) -> String? {
    // TODO: should be localized string resource
    return { count: Long? -> count?.let { formatShortFileSize(context, it) } }
}

fun formatDateTimeFunction(context: Context): (Instant?) -> String? {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm z").withZone(ZoneId.systemDefault())
    return { dateTime: Instant? -> dateTime?.let { formatter.format(it) } }
}