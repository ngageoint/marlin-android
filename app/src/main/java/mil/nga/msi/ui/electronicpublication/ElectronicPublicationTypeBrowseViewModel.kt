@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package mil.nga.msi.ui.electronicpublication

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.FileProvider
import androidx.core.content.IntentCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationTypeBrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ePubRepo: ElectronicPublicationRepository,
) : ViewModel() {

    private val pubTypeArg: Int = checkNotNull(savedStateHandle["pubType"], { "missing pubType argument" })
    val pubTypeState: State<ElectronicPublicationType> = derivedStateOf { ElectronicPublicationType.fromTypeCode(pubTypeArg) }

    private val publications = ePubRepo.observeElectronicPublicationsOfType(pubTypeState.value)
        .onEach { pubs ->
            mutableCurrentNodeState.update {
                if (it is PublicationsLoadingNode) {
                    val parent = it.parent
                    parent ?: PublicationTypeRootNode(pubTypeState.value)
                }
                else {
                    it
                }
            }
        }

    private val mutableCurrentNodeState: MutableStateFlow<PublicationBrowsingNode> = MutableStateFlow(PublicationsLoadingNode(null, pubTypeState.value))
    val currentNodeState = mutableCurrentNodeState.asStateFlow()

    val currentNodeLinksState = combine(publications, currentNodeState) { pubs, currentNode -> linksForNode(currentNode, pubs) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Publications(emptyList()))

    init {
        viewModelScope.launch {
            publications.collect()
        }
        viewModelScope.launch {
            while (isActive) {
                ePubRepo.updateDownloadProgress()
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

    fun onDownloadClick(ePub: ElectronicPublication) {
        viewModelScope.launch { ePubRepo.download(ePub) }
    }

    fun onCancelDownloadClick(ePub: ElectronicPublication) {
        TODO("Not yet implemented")
    }

    fun onDeleteClick(ePub: ElectronicPublication) {
    }

    fun uriToSharePublication(ePub: ElectronicPublication): Uri = ePubRepo.getContentUriToSharePublication(ePub)
}

fun linksForNode(node: PublicationBrowsingNode, ePubs: List<ElectronicPublication>): PublicationBrowsingLinksArrangement {
    return when (node) {
        is PublicationTypeRootNode -> linksForPubType(node.pubType, ePubs)
        is PublicationFolderNode -> linksForFolder(node, ePubs)
        is PublicationsLoadingNode -> Publications(emptyList())
    }
}

fun linksForPubType(pubType: ElectronicPublicationType, publications: List<ElectronicPublication>): PublicationBrowsingLinksArrangement {
    return when (pubType) {
        ElectronicPublicationType.AtlasOfPilotCharts,
        ElectronicPublicationType.ListOfLights,
        ElectronicPublicationType.SightReductionTablesForMarineNavigation, -> {
            groupIntoFoldersByDownloadId(publications)
        }
        ElectronicPublicationType.AmericanPracticalNavigator,
        ElectronicPublicationType.UscgLightList, ->
            PublicationSections(listOf(
                PublicationSection("Complete Volumes", publications.map(::PublicationLink))
            ))
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
            Publications(publications.map(::PublicationLink))
        }
    }
}

fun linksForFolder(folder: PublicationFolderNode, publications: List<ElectronicPublication>): Publications {
    return Publications(publications.filter { it.pubDownloadId == folder.pubDownloadId } .sortedBy { it.sectionOrder } .map(::PublicationLink))
}

sealed class PublicationBrowsingNode(
    open val parent: PublicationBrowsingNode?,
    val title: String,
)

class PublicationsLoadingNode(parent: PublicationBrowsingNode?, val pubType: ElectronicPublicationType) :
    PublicationBrowsingNode(parent, pubType.label)

class PublicationTypeRootNode(
    val pubType: ElectronicPublicationType,
) : PublicationBrowsingNode(null, pubType.label)

class PublicationFolderNode(
    val pubDownloadId: Int,
    val pubDownloadDisplayName: String,
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
    val sectionPubs: MutableMap<String, MutableList<PublicationLink>> = fold(mutableMapOf()) { sections, pub ->
        val key = pub.discriminator()
        val sectionTitle = sectionTitleMap[key]
        if (sectionTitle != null) {
            val sectionPubs = sections[sectionTitle] ?: mutableListOf()
            sectionPubs.add(PublicationLink(pub))
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
    val folderLinks: Map<Int, PublicationFolderLink> = pubs.fold(emptyMap()) { folderLinks, pub ->
        if (pub.pubDownloadId == null) {
            return@fold folderLinks
        }
        val folderLink = folderLinks.get(pub.pubDownloadId)
        val title = if (folderLink?.title.isNullOrBlank()) pub.pubDownloadDisplayName ?: "" else folderLink?.title ?: ""
        folderLinks + (pub.pubDownloadId to
            (folderLink?.copy(fileCount = folderLink.fileCount + 1, title = title)
                ?: PublicationFolderLink(pub.pubDownloadId, pub.pubDownloadOrder, pub.pubDownloadDisplayName ?: "", 1)))
    }
    return PublicationFolders(folderLinks.values.sortedBy { it.pubDownloadOrder })
}

sealed interface PublicationBrowsingLink
data class PublicationFolderLink(val pubDownloadId: Int, val pubDownloadOrder: Int?, val title: String, val fileCount: Int) : PublicationBrowsingLink
data class PublicationLink(val publication: ElectronicPublication) : PublicationBrowsingLink

