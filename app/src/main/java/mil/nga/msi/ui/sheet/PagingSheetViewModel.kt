package mil.nga.msi.ui.sheet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.ui.map.AnnotationProvider
import javax.inject.Inject

@HiltViewModel
class PagingSheetViewModel @Inject constructor(
   val annotationProvider: AnnotationProvider
): ViewModel()