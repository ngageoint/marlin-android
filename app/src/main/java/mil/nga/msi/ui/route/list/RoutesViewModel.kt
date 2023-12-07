package mil.nga.msi.ui.route.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.repository.route.RouteRepository
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    val routes = repository.observeRoutesWithWaypoints().asLiveData()

    suspend fun delete(route: Route) = repository.delete(route)
}