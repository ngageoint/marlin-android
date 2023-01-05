@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package mil.nga.msi.ui.electronicpublication

import android.text.format.Formatter.formatShortFileSize
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationTypeBrowseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ePubRepo: ElectronicPublicationRepository,
) : ViewModel() {

    private val pubTypeArg: Int = checkNotNull(savedStateHandle["pubType"], { "missing pubType argument" })
    val pubTypeState: State<ElectronicPublicationType> = derivedStateOf { ElectronicPublicationType.fromTypeCode(pubTypeArg) }

    private val publications = ePubRepo.observeElectronicPublicationsOfType(pubTypeState.value)
        .onEach { pubs ->
            mutableCurrentNodeState.update { PublicationTypeRootNode(pubTypeState.value, linksForPubType(pubTypeState.value, pubs)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val mutableCurrentNodeState: MutableStateFlow<PublicationBrowsingNode> = MutableStateFlow(PublicationsLoadingNode(null, pubTypeState.value))
    val currentNodeState = mutableCurrentNodeState.asStateFlow()

    init {
        viewModelScope.launch {
            publications.collect()
        }
    }

    fun backToParent() {
        mutableCurrentNodeState.update { it.parent ?: it }
    }

    fun onFolderLinkClick(link: PublicationFolderLink) {
        mutableCurrentNodeState.update {
            val pubsInFolder = linksForFolder(link, publications.value)
            val folderNode = PublicationFolderNode(link.pubDownloadId, link.title, currentNodeState.value, pubsInFolder)
            folderNode
        }
    }

    fun onDownloadClick(ePub: ElectronicPublication) {

    }

    fun onDeleteClick(ePub: ElectronicPublication) {

    }

    fun onOpenEPubClick(ePub: ElectronicPublication) {

    }
}

fun linksForPubType(pubType: ElectronicPublicationType, publications: List<ElectronicPublication>): PublicationBrowsingLinkList {
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

fun linksForFolder(folder: PublicationFolderLink, publications: List<ElectronicPublication>): Publications {
    return Publications(publications.filter { it.pubDownloadId == folder.pubDownloadId } .sortedBy { it.sectionOrder } .map(::PublicationLink))
}

sealed class PublicationBrowsingNode(
    open val parent: PublicationBrowsingNode?,
    val title: String,
    val links: PublicationBrowsingLinkList,
)

class PublicationsLoadingNode(parent: PublicationBrowsingNode?, val pubType: ElectronicPublicationType) :
    PublicationBrowsingNode(parent, pubType.label, Publications(emptyList()))

class PublicationTypeRootNode(
    val pubType: ElectronicPublicationType,
    links: PublicationBrowsingLinkList,
) : PublicationBrowsingNode(null, pubType.label, links)

class PublicationFolderNode(
    val pubDownloadId: Int,
    val pubDownloadDisplayName: String,
    parent: PublicationBrowsingNode,
    links: PublicationBrowsingLinkList,
) : PublicationBrowsingNode(parent, pubDownloadDisplayName, links)

sealed class PublicationBrowsingLinkList
class Publications(val publications: List<PublicationLink>) : PublicationBrowsingLinkList()
class PublicationSections(val sections: List<PublicationSection>) : PublicationBrowsingLinkList()
class PublicationFolders(val folders: List<PublicationFolderLink>) : PublicationBrowsingLinkList()

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

