package mil.nga.msi.ui.navigation

import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.electronicpublication.ElectronicPublicationRoute
import mil.nga.msi.ui.geopackage.GeoPackageRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

private val mainRoutes: Map<DataSource, Route> = DataSource.values().fold(mapOf()) { dataSourceToRoute, dataSource ->
    val route = when (dataSource) {
        DataSource.ASAM -> AsamRoute.Main
        DataSource.MODU -> ModuRoute.Main
        DataSource.NAVIGATION_WARNING -> NavigationWarningRoute.Main
        DataSource.LIGHT -> LightRoute.Main
        DataSource.PORT -> PortRoute.Main
        DataSource.RADIO_BEACON -> RadioBeaconRoute.Main
        DataSource.DGPS_STATION -> DgpsStationRoute.Main
        DataSource.ELECTRONIC_PUBLICATION -> ElectronicPublicationRoute.Main
        DataSource.NOTICE_TO_MARINERS -> NoticeToMarinersRoute.Main
        DataSource.GEOPACKAGE -> GeoPackageRoute.Sheet
    }
    dataSourceToRoute + (dataSource to route)
}

fun mainRouteFor(dataSource: DataSource): Route {
    return mainRoutes[dataSource]!!
}