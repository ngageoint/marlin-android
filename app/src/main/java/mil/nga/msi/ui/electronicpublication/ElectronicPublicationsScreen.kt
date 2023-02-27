package mil.nga.msi.ui.electronicpublication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.ui.main.TopBar

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ElectronicPublicationsScreen(
    openDrawer: () -> Unit,
    onPubTypeClick: (ElectronicPublicationType) -> Unit,
    viewModel: ElectronicPublicationsViewModel = hiltViewModel()
) {
    val pubTypes by viewModel.publicationTypes.collectAsStateWithLifecycle(initialValue = emptyList())
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = ElectronicPublicationRoute.List.title,
            navigationIcon = Icons.Filled.Menu,
            onNavigationClicked = { openDrawer() },
        )
        ElectronicPublicationTypeList(pubTypes = pubTypes, onPubTypeClick = onPubTypeClick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ElectronicPublicationTypeList(pubTypes: List<PublicationTypeListItem>, onPubTypeClick: (ElectronicPublicationType) -> Unit) {
    Surface {
        LazyColumn {
            pubTypes.forEach {
                item {
                    ListItem(
                        text = { Text(it.pubType.label) },
                        secondaryText = { Text("${it.fileCount} files") },
                        icon = {
                            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = it.pubType.label
                                )
                            }
                        },
                        trailing = {
                            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = it.pubType.label
                                )
                            }
                        },
                        modifier = Modifier
                            .clickable(onClick = { onPubTypeClick(it.pubType) })
                            .padding(bottom = 8.dp)
                    )
                    Divider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}



@Composable
@Preview
fun PreviewElectronicPublicationTypeList() {
    ElectronicPublicationTypeList(onPubTypeClick = {},
        pubTypes = ElectronicPublicationType.values().map { PublicationTypeListItem(it, 2 * it.typeId) })
}