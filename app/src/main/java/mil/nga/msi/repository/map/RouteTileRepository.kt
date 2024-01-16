package mil.nga.msi.repository.map

import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.route.RouteCreationRepository
import mil.nga.msi.repository.route.RouteLocalDataSource
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.RouteImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class RouteCreationTileRepository @Inject constructor(
    private val routeCreationRepository: RouteCreationRepository
): TileRepository {
    override suspend fun getTileableItems(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): List<DataSourceImage> {
        return routeCreationRepository.route.value?.let { route ->
            val routeMinLongitude = route.minLongitude
            val routeMaxLongitude = route.maxLongitude
            val routeMinLatitude = route.minLatitude
            val routeMaxLatitude = route.maxLatitude

            if ((routeMinLongitude != null && routeMinLongitude <= maxLongitude) &&
                (routeMaxLongitude != null && routeMaxLongitude >= minLongitude) &&
                (routeMinLatitude != null && routeMinLatitude <= maxLatitude) &&
                (routeMaxLatitude != null && routeMaxLatitude >= minLatitude)) {
                route.getFeatures().map {
                    RouteImage(it)
                }
            } else {
                emptyList()
            }
        } ?: emptyList()
    }
}

class RoutesTileRepository @Inject constructor(
    private val localDataSource: RouteLocalDataSource,
    private val filterRepository: FilterRepository
): TileRepository {

    override suspend fun getTileableItems(
        minLatitude: Double,
        maxLatitude: Double,
        minLongitude: Double,
        maxLongitude: Double
    ): List<DataSourceImage> {
        return localDataSource.getRoutes(
            minLatitude = minLatitude,
            minLongitude = minLongitude,
            maxLatitude = maxLatitude,
            maxLongitude = maxLongitude
        ).flatMap { route ->
            route.getFeatures().map { RouteImage(it) }
        }
    }
}