package mil.nga.msi.ui.route.sheet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.repository.route.RouteCreationRepository
import javax.inject.Inject

@HiltViewModel
class RouteBottomSheetViewModel @Inject constructor(
    private val routeCreationRepository: RouteCreationRepository
): ViewModel() {
    fun addWaypoint(waypoint: RouteWaypoint) = routeCreationRepository.addWaypoint(waypoint)
}