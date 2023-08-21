package mil.nga.msi.ui.electronicpublication

import android.content.Intent
import android.text.format.Formatter
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.ui.main.TopBar

@Composable
fun ElectronicPublicationDetailScreen(
    s3Key: String,
    onBack: () -> Unit,
    onBookmark: (ElectronicPublication) -> Unit,
    viewModel: ElectronicPublicationViewModel = hiltViewModel()
) {
    val publicationWithBookmark by viewModel.publicationWithBookmark.observeAsState()
    viewModel.setPublicationKey(s3Key)

    val context = LocalContext.current

    Column {
        TopBar(
            title = publicationWithBookmark?.electronicPublication?.sectionDisplayName.orEmpty(),
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationClicked = onBack,
        )

        PublicationDetail(
            publicationWithBookmark = publicationWithBookmark,
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
}

@Composable
fun PublicationDetail(
    publicationWithBookmark: ElectronicPublicationWithBookmark?,
    onAction: (PublicationAction) -> Unit
) {
    publicationWithBookmark?.let { (publication, bookmark) ->
        val fileType = publication.fileExtension?.toUpperCase(Locale.current) ?: "Unknown file type"

        val fileSize = publication.fileSize?.let {size ->
            Formatter.formatShortFileSize(LocalContext.current, size)
        } ?: "Unknown size"

        val uploadTime = publication.uploadTime?.let {
            "Uploaded ${ElectronicPublication.DATE_TIME_FORMATTER.format(publication.uploadTime)}"
        } ?: "Unknown upload time"

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = publication.sectionDisplayName ?: ""
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = "$fileType - $fileSize"
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = uploadTime
                )
            }

            ElectronicPublicationFooter(publicationWithBookmark = publicationWithBookmark, onAction = onAction)
        }
    }
}