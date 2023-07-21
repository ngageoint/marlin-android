package mil.nga.msi.ui.electronicpublication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import javax.inject.Inject

@HiltViewModel
class ElectronicPublicationViewModel @Inject constructor(
   private val electronicPublicationRepository: ElectronicPublicationRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   private val _s3KeyFlow = MutableSharedFlow<String>(replay = 1)
   fun setPublicationKey(key: String) {
      viewModelScope.launch {
         _s3KeyFlow.emit(key)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val publicationWithBookmark = _s3KeyFlow.flatMapLatest { s3Key ->
      combine(
         electronicPublicationRepository.observeElectronicPublication(s3Key),
         bookmarkRepository.observeBookmark(DataSource.ELECTRONIC_PUBLICATION, s3Key)
      ) { publication, bookmark ->
         ElectronicPublicationWithBookmark(publication, bookmark)
      }
   }.asLiveData()

   fun publicationShareUri(publication: ElectronicPublication) =
      electronicPublicationRepository.getContentUriToSharePublication(publication)


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
}