@file:OptIn(ExperimentalFoundationApi::class)

package mil.nga.msi.ui.electronicpublication

import android.content.Context
import android.content.Intent
import android.text.format.Formatter.formatShortFileSize
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

interface PublicationActions {
    fun onDownloadClick(ePub: ElectronicPublication)
    fun onCancelDownloadClick(ePub: ElectronicPublication)
    fun onViewClick(ePub: ElectronicPublication)
    fun onDeleteClick(ePub: ElectronicPublication)
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ElectronicPublicationTypeBrowseRoute(
    onBackToRoot: () -> Unit,
    viewModel: ElectronicPublicationTypeBrowseViewModel = hiltViewModel()
) {
    val currentNodeState by viewModel.currentNodeState.collectAsStateWithLifecycle()
    val currentNodeLinks by viewModel.currentNodeLinksState.collectAsStateWithLifecycle()
    val onBackClick = {
        when (currentNodeState.parent) {
            null -> onBackToRoot()
            else -> viewModel.backToParent()
        }
    }
    // TODO: how will this work when time zone changes, or when offline?
    val formatDateTime = formatDateTimeFunction()
    val formatByteCount = formatByteCountFunction(LocalContext.current)
    val context = LocalContext.current
    BackHandler(onBack = onBackClick)
    ElectronicPublicationTypeBrowseScreen(
        currentNode = currentNodeState,
        currentNodeLinks = currentNodeLinks,
        onLinkClick = { link -> if (link is PublicationFolderLink) viewModel.onFolderLinkClick(link) },
        onBackClick = onBackClick,
        formatDateTime = formatDateTime,
        formatByteCount = formatByteCount,
        publicationActions = object : PublicationActions {
            override fun onDownloadClick(ePub: ElectronicPublication) = viewModel.onDownloadClick(ePub)
            override fun onCancelDownloadClick(ePub: ElectronicPublication) = viewModel.onCancelDownloadClick(ePub)
            override fun onDeleteClick(ePub: ElectronicPublication) = viewModel.onDeleteClick(ePub)
            override fun onViewClick(ePub: ElectronicPublication) {
                val ePubUri = viewModel.uriToSharePublication(ePub)
                val viewEPub = Intent(Intent.ACTION_VIEW)
                    .setDataAndType(ePubUri, ePub.downloadMediaType)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(viewEPub)
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
    publicationActions: PublicationActions,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = currentNode.title,
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationClicked = onBackClick,
        )
        Surface {
            when (currentNodeLinks) {
                is Publications -> {
                    PublicationList(
                        publicationLinks = currentNodeLinks,
                        formatDateTime = formatDateTime,
                        formatByteCount = formatByteCount,
                        actions = publicationActions
                    )
                }
                is PublicationSections -> {
                    PublicationSectionsList(
                        sections = currentNodeLinks.sections,
                        formatDateTime = formatDateTime,
                        formatByteCount = formatByteCount,
                        actions = publicationActions
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

@Composable
fun PublicationSectionsList(
    sections: List<PublicationSection>,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
    actions: PublicationActions,
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
                                .background(MaterialTheme.colorScheme.screenBackground)
                                .padding(6.dp)
                                .fillMaxWidth(),
                            text = section.title,
                        )
                    }
                }
            }
            items(section.publications) { pubLink ->
                PublicationListItem(
                    ePub = pubLink.publication,
                    formatDateTime = formatDateTime,
                    formatByteCount = formatByteCount,
                    actions = actions,
                )
                if (pubLink != section.publications.last()) {
                    Divider(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
        item {
            Divider()
        }
    }
}

@Composable
fun PublicationListItem(
    ePub: ElectronicPublication,
    formatDateTime: (Instant?) -> String?, formatByteCount: (Long?) -> String?,
    actions: PublicationActions) {

    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = ePub.sectionDisplayName ?: ""
        )
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = "${ePub.fileExtension?.toUpperCase(Locale.current) ?: "Unknown file type"} - ${formatByteCount(ePub.fileSize) ?: "Unknown size"}"
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = ePub.uploadTime?.let { "Uploaded ${formatDateTime(ePub.uploadTime)}" } ?: "Unknown upload time"
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            Arrangement.End
        ) {
            if (ePub.isDownloaded) {
                TextButton(
                    onClick = { actions.onViewClick(ePub) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("View", style = MaterialTheme.typography.labelLarge)
                }
                TextButton(
                    onClick = { actions.onDeleteClick(ePub) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Delete", style = MaterialTheme.typography.labelLarge)
                }
            } else if (ePub.isDownloading) {
                val progress = ePub.fileSize?.let {
                    ePub.downloadedBytes.toDouble() / ePub.fileSize
                } ?: -1.0
                if (progress > -1) {
                    LinearProgressIndicator(
                        progress = progress.toFloat(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                TextButton(
                    onClick = { actions.onCancelDownloadClick(ePub) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                TextButton(
                    onClick = { actions.onDownloadClick(ePub) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Download", style = MaterialTheme.typography.labelLarge)
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
        formatByteCount = { "${it ?: "Unknown size"}" },
        actions = object : PublicationActions {
            override fun onDownloadClick(ePub: ElectronicPublication) {}
            override fun onCancelDownloadClick(ePub: ElectronicPublication) {}
            override fun onViewClick(ePub: ElectronicPublication) {}
            override fun onDeleteClick(ePub: ElectronicPublication) {}
        }
    )
}

@Preview(name = "Publication List Item Downloading Percent Progress")
@Composable
fun PreviewPublicationListItemDownloadingPercentProgress() {
    PublicationListItem(
        ePub = ElectronicPublication(
            s3Key = "pub1",
            sectionDisplayName = "Publication 1",
            fullFilename = "pub1.pdf",
            fileExtension = "pdf",
            fileSize = 13_003_246,
            uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"),
            isDownloading = true,
            downloadedBytes = 3_456_678,
        ),
        formatDateTime = { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withZone(ZoneId.of("UTC")).format(it) },
        formatByteCount = { "${it ?: "Unknown size"}" },
        actions = object : PublicationActions {
            override fun onDownloadClick(ePub: ElectronicPublication) {}
            override fun onCancelDownloadClick(ePub: ElectronicPublication) {}
            override fun onViewClick(ePub: ElectronicPublication) {}
            override fun onDeleteClick(ePub: ElectronicPublication) {}
        }
    )
}

@Preview(name = "Publication List Item Downloading Unknown Progress")
@Composable
fun PreviewPublicationListItemDownloadingUnknownProgress() {
    PublicationListItem(
        ePub = ElectronicPublication(
            s3Key = "pub1",
            sectionDisplayName = "Publication 1",
            fullFilename = "pub1.pdf",
            fileExtension = "pdf",
            uploadTime = Instant.parse("2022-12-25T12:25:00.0Z"),
            isDownloading = true,
            downloadedBytes = 3_456_678,
        ),
        formatDateTime = { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withZone(ZoneId.of("UTC")).format(it) },
        formatByteCount = { "${it ?: "Unknown size"}" },
        actions = object : PublicationActions {
            override fun onDownloadClick(ePub: ElectronicPublication) {}
            override fun onCancelDownloadClick(ePub: ElectronicPublication) {}
            override fun onViewClick(ePub: ElectronicPublication) {}
            override fun onDeleteClick(ePub: ElectronicPublication) {}
        }
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
        formatDateTime = { it?.toString() ?: "Unknown upload time" },
        formatByteCount = { "$it bytes" },
        actions = object : PublicationActions {
            override fun onDownloadClick(ePub: ElectronicPublication) {}
            override fun onCancelDownloadClick(ePub: ElectronicPublication) {}
            override fun onViewClick(ePub: ElectronicPublication) {}
            override fun onDeleteClick(ePub: ElectronicPublication) {}
        }
    )
}

@Composable
fun PublicationList(
    publicationLinks: Publications,
    formatDateTime: (Instant?) -> String?,
    formatByteCount: (Long?) -> String?,
    actions: PublicationActions,
) {
    Surface(Modifier.fillMaxSize()) {
        LazyColumn {
            publicationLinks.publications.forEachIndexed { pos, pubLink ->
                item {
                    PublicationListItem(
                        ePub = pubLink.publication,
                        formatDateTime = formatDateTime,
                        formatByteCount = formatByteCount,
                        actions = actions,
                    )
                    if (pos < publicationLinks.publications.size - 1) {
                        Divider(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
            item {
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
                Divider(Modifier.padding(start = 16.dp))
            }
        }
    }
}

fun formatByteCountFunction(context: Context): (Long?) -> String? {
    // TODO: should be localized string resource
    return { count: Long? -> count?.let { formatShortFileSize(context, it) } }
}

fun formatDateTimeFunction(): (Instant?) -> String? {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm z").withZone(ZoneId.systemDefault())
    return { dateTime: Instant? -> dateTime?.let { formatter.format(it) } }
}