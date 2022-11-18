package mil.nga.msi.ui.electronicpublication

import androidx.compose.ui.graphics.Color
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.navigation.Route

sealed class ElectronicPublicationRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
    override val color: Color = DataSource.ELECTRONIC_PUBLICATION.color
): Route {
    object Main: ElectronicPublicationRoute("epubs", "Electronic Publications", "E-Pubs")
    object Detail: ElectronicPublicationRoute("epubs/detail", "Electronic Publication Details", "E-Pub Details")
    object List: ElectronicPublicationRoute("epubs/list", "Electronic Publications", "E-Pubs")
}