package mil.nga.msi.ui.electronicpublication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationsViewModel @Inject constructor(
    private val repository: ElectronicPublicationRepository
) : ViewModel() {

    val publicationTypes = repository.observeFileCountsByType().map {
        it.map { PublicationTypeNode(it.key, it.value) } .sortedBy { it.pubType.toString() }
    }

    fun publicationsOfType(pubType: ElectronicPublicationType) : Unit {
    }
}

data class PublicationTypeNode(
    val pubType: ElectronicPublicationType,
    val fileCount: Int
)

data class PublicationTreeNode(
    val pubType: ElectronicPublicationType,
)

data class PublicationSections(val sections: List<PublicationSection>)

data class PublicationSection(val title: String, val publications: List<ElectronicPublication>)
