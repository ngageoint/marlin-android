package mil.nga.msi.ui.electronicpublication

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationTypeBrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val electronicPublicationRepository: ElectronicPublicationRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val pubTypeArg: Int = checkNotNull(savedStateHandle["pubType"]) { "missing pubType argument" }
    private val pubTypeState: State<ElectronicPublicationType> = derivedStateOf { ElectronicPublicationType.fromTypeCode(pubTypeArg) }

    private val publications = electronicPublicationRepository.observeElectronicPublicationsOfType(pubTypeState.value)
        .onEach {
            mutableCurrentNodeState.update {
                if (it is PublicationsLoadingNode) {
                    val parent = it.parent
                    parent ?: PublicationTypeRootNode(pubTypeState.value)
                } else {
                    it
                }
            }
        }

    private val mutableCurrentNodeState: MutableStateFlow<PublicationBrowsingNode> = MutableStateFlow(PublicationsLoadingNode(null, pubTypeState.value))
    val currentNodeState = mutableCurrentNodeState.asStateFlow()

    val currentNodeLinksState =
        combine(
            publications,
            currentNodeState,
            bookmarkRepository.observeBookmarks(DataSource.ELECTRONIC_PUBLICATION)
        ) { pubs, currentNode, _ ->
            linksForNode(currentNode, pubs)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Publications(emptyList()))

    init {
        viewModelScope.launch {
            publications.collect()
        }
        viewModelScope.launch {
            while (isActive) {
                electronicPublicationRepository.updateDownloadProgress()
                delay(1000)
            }
        }
    }

    fun backToParent() {
        mutableCurrentNodeState.update { it.parent ?: it }
    }

    fun onFolderLinkClick(link: PublicationFolderLink) {
        mutableCurrentNodeState.update {
            val folderNode = PublicationFolderNode(link.pubDownloadId, link.title, it)
            folderNode
        }
    }

    fun download(ePub: ElectronicPublication) {
        viewModelScope.launch { electronicPublicationRepository.download(ePub) }
    }

    fun cancelDownload(ePub: ElectronicPublication) {
        viewModelScope.launch {
            electronicPublicationRepository.cancelDownload(ePub)
        }
    }

    fun delete(ePub: ElectronicPublication) {
        viewModelScope.launch {
            electronicPublicationRepository.removeDownload(ePub)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.delete(bookmark)
        }
    }

    fun publicationShareUri(publication: ElectronicPublication) =
        electronicPublicationRepository.getContentUriToSharePublication(publication)

    private suspend fun linksForPubType(pubType: ElectronicPublicationType, publications: List<ElectronicPublication>): PublicationBrowsingLinksArrangement {
        return when (pubType) {
            ElectronicPublicationType.AtlasOfPilotCharts,
            ElectronicPublicationType.ListOfLights,
            ElectronicPublicationType.SightReductionTablesForMarineNavigation, -> {
                groupIntoFoldersByDownloadId(publications)
            }
            ElectronicPublicationType.AmericanPracticalNavigator,
            ElectronicPublicationType.UscgLightList, -> {
                PublicationSections(
                    listOf(
                        PublicationSection(
                            title = "Complete Volumes",
                            publications = publications.map { publication ->
                                val bookmark = bookmarkRepository.getBookmark(DataSource.ELECTRONIC_PUBLICATION, publication.s3Key)
                                PublicationLink(
                                    ElectronicPublicationWithBookmark(publication, bookmark)
                                )
                            }
                        )
                    )
                )
            }
            ElectronicPublicationType.ChartNo1,
            ElectronicPublicationType.SailingDirectionsEnroute,
            ElectronicPublicationType.SailingDirectionsPlanningGuides,
            ElectronicPublicationType.SightReductionTablesForAirNavigation, -> {
                publications.sectionBy(
                    { fullPubFlag },
                    true to "Complete Volumes",
                    false to "Single Chapters",
                )
            }
            ElectronicPublicationType.DistanceBetweenPorts,
            ElectronicPublicationType.InternationalCodeOfSignals,
            ElectronicPublicationType.RadioNavigationAids,
            ElectronicPublicationType.RadarNavigationAndManeuveringBoardManual, -> {
                publications.sectionBy(
                    { fullPubFlag },
                    true to "Complete Volume",
                    false to "Single Chapters",
                )
            }
            ElectronicPublicationType.WorldPortIndex -> {
                publications.sectionBy(
                    { fullPubFlag },
                    true to "Complete Volume",
                    false to "Additional Formats",
                )
            }
            ElectronicPublicationType.FleetGuides,
            ElectronicPublicationType.NoaaTidalCurrentTables,
            ElectronicPublicationType.NoticeToMarinersAndCorrections,
            ElectronicPublicationType.Random,
            ElectronicPublicationType.TideTables,
            ElectronicPublicationType.Unknown, -> {
                Publications(
                    publications = publications.map { publication ->
                        val bookmark = bookmarkRepository.getBookmark(DataSource.ELECTRONIC_PUBLICATION, publication.s3Key)
                        PublicationLink(
                            ElectronicPublicationWithBookmark(publication, bookmark)
                        )
                    }
                )
            }
        }
    }

    private suspend fun linksForNode(node: PublicationBrowsingNode, ePubs: List<ElectronicPublication>): PublicationBrowsingLinksArrangement {
        return when (node) {
            is PublicationTypeRootNode -> linksForPubType(node.pubType, ePubs)
            is PublicationFolderNode -> linksForFolder(node, ePubs)
            is PublicationsLoadingNode -> Publications(emptyList())
        }
    }

    private suspend fun linksForFolder(folder: PublicationFolderNode, publications: List<ElectronicPublication>): Publications {
        return Publications(
            publications = publications
                .filter { it.pubDownloadId == folder.pubDownloadId }
                .sortedBy { it.sectionOrder }
                .map { publication ->
                    val bookmark = bookmarkRepository.getBookmark(DataSource.ELECTRONIC_PUBLICATION, publication.s3Key)
                    PublicationLink(
                        ElectronicPublicationWithBookmark(publication, bookmark)
                    )
                }
        )
    }
}





sealed class PublicationBrowsingNode(
    open val parent: PublicationBrowsingNode?,
    val title: String,
)

class PublicationsLoadingNode(parent: PublicationBrowsingNode?, pubType: ElectronicPublicationType) :
    PublicationBrowsingNode(parent, pubType.label)

class PublicationTypeRootNode(
    val pubType: ElectronicPublicationType,
) : PublicationBrowsingNode(null, pubType.label)

class PublicationFolderNode(
    val pubDownloadId: Int,
    pubDownloadDisplayName: String,
    parent: PublicationBrowsingNode,
) : PublicationBrowsingNode(parent, pubDownloadDisplayName)

sealed class PublicationBrowsingLinksArrangement
class Publications(val publications: List<PublicationLink>) : PublicationBrowsingLinksArrangement()
class PublicationSections(val sections: List<PublicationSection>) : PublicationBrowsingLinksArrangement()
class PublicationFolders(val folders: List<PublicationFolderLink>) : PublicationBrowsingLinksArrangement()

data class PublicationSection(val title: String, val publications: List<PublicationLink>) {
    override fun equals(other: Any?): Boolean {
        return other is PublicationSection && other.title == title
    }
    override fun hashCode(): Int {
        return title.hashCode()
    }
}

fun <T> List<ElectronicPublication>.sectionBy(discriminator: ElectronicPublication.() -> T, vararg sectionTitles: Pair<T, String>): PublicationSections {
    val sectionTitleMap = buildMap(sectionTitles.size) { putAll(sectionTitles) }
    val sectionPubs: MutableMap<String, MutableList<PublicationLink>> = fold(mutableMapOf()) { sections, publication ->
        val key = publication.discriminator()
        val sectionTitle = sectionTitleMap[key]
        if (sectionTitle != null) {
            val sectionPubs = sections[sectionTitle] ?: mutableListOf()
            sectionPubs.add(
                PublicationLink(
                    ElectronicPublicationWithBookmark(publication)
                )
            )
            sections[sectionTitle] = sectionPubs
        }
        sections
    }
    val sectionsOrdered: MutableList<PublicationSection> = sectionTitles.fold(mutableListOf()) { sections, (_, title) ->
        sectionPubs[title]?.let {
            sections.add(PublicationSection(title, it))
        }
        sections
    }
    return PublicationSections(sectionsOrdered)
}

fun groupIntoFoldersByDownloadId(pubs: List<ElectronicPublication>): PublicationFolders {
    val folderLinks: Map<Int, PublicationFolderLink> = pubs.fold(emptyMap()) { folderLinks, publication ->
        if (publication.pubDownloadId == null) {
            return@fold folderLinks
        }
        val folderLink = folderLinks[publication.pubDownloadId]
        val title = if (folderLink?.title.isNullOrBlank()) publication.pubDownloadDisplayName ?: "" else folderLink?.title ?: ""
        folderLinks + (publication.pubDownloadId to
            (folderLink?.copy(fileCount = folderLink.fileCount + 1, title = title)
                ?: PublicationFolderLink(publication.pubDownloadId, publication.pubDownloadOrder, publication.pubDownloadDisplayName ?: "", 1)))
    }
    return PublicationFolders(folderLinks.values.sortedBy { it.pubDownloadOrder })
}

sealed interface PublicationBrowsingLink
data class PublicationFolderLink(val pubDownloadId: Int, val pubDownloadOrder: Int?, val title: String, val fileCount: Int) : PublicationBrowsingLink
data class PublicationLink(val publicationWithBookmark: ElectronicPublicationWithBookmark) : PublicationBrowsingLink

