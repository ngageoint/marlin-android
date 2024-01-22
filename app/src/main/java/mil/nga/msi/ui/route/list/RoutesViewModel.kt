package mil.nga.msi.ui.route.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
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

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {

    private val routeIdFlow = MutableSharedFlow<Long>(replay = 1)
    fun setRouteId(routeId: Long) {
        viewModelScope.launch {
            routeIdFlow.emit(routeId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val route = routeIdFlow.flatMapLatest { routeId ->
        repository.observeRouteWithWaypoints(routeId)
    }.asLiveData()
}