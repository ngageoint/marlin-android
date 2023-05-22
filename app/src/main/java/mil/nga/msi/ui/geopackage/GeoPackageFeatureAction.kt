package mil.nga.msi.ui.geopackage

import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.navigation.NavPoint

sealed class GeoPackageFeatureAction {
   class Zoom(val point: NavPoint): GeoPackageFeatureAction()
   class Location(val text: String): GeoPackageFeatureAction()
   class Media(val key: GeoPackageMediaKey): GeoPackageFeatureAction()
}