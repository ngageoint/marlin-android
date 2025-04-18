package mil.nga.msi.ui.electronicpublication

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationsViewModel @Inject constructor(
    repository: ElectronicPublicationRepository
) : ViewModel() {

    val publicationTypes = repository.observeFileCountsByType().map {
        it.map { (pubType, count) ->
            PublicationTypeListItem(pubType, count)
        }.sortedBy { item -> item.pubType.toString() }
    }
}

data class PublicationTypeListItem(
    val pubType: ElectronicPublicationType,
    val fileCount: Int
)
