package mil.nga.msi.ui.electronicpublication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ElectronicPublicationsScreen(
    openDrawer: () -> Unit,
    onTap: (ElectronicPublicationType) -> Unit,
    viewModel: ElectronicPublicationsViewModel = hiltViewModel()
) {
    val pubTypeNodes by viewModel.publicationTypes.collectAsStateWithLifecycle(initialValue = emptyList())
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = ElectronicPublicationRoute.List.title,
            navigationIcon = Icons.Filled.Menu,
            onNavigationClicked = { openDrawer() },
        )
        Surface(color = MaterialTheme.colors.screenBackground) {
            LazyColumn {
                pubTypeNodes.forEach {
                    item {
                        ListItem(
                            text = { Text(it.pubType.label) },
                            secondaryText = { Text("${it.fileCount} files") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = it.pubType.label
                                )
                            },
                            trailing = {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = it.pubType.label
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = { onTap(it.pubType) }
                            )
                        )
                    }
                }
            }
        }
    }
}



@Composable
@Preview
fun PreviewElectronicPublicationsScreen() {
    ElectronicPublicationsScreen(openDrawer = {}, onTap = {})
}