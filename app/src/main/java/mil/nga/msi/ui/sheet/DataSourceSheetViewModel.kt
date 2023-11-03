package mil.nga.msi.ui.sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.BottomSheetRepository
import mil.nga.msi.ui.map.AnnotationProvider
import javax.inject.Inject

@HiltViewModel
class DataSourceSheetViewModel @Inject constructor(
   val annotationProvider: AnnotationProvider,
   private val bookmarkRepository: BookmarkRepository,
   bottomSheetRepository: BottomSheetRepository
   ): ViewModel() {
   val mapAnnotations = bottomSheetRepository.mapAnnotations

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}