package mil.nga.msi.ui.electronicpublication

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.bookmark.BookmarkRoute
import mil.nga.msi.ui.navigation.Route

sealed class ElectronicPublicationRoute(
    override val name: String,
    override val title: String = "Electronic Publications",
    override val shortTitle: String = "E-Pubs",
): Route {
    override val color: Color = DataSource.ELECTRONIC_PUBLICATION.color
    data object Main: ElectronicPublicationRoute("electronicPublications")
    data object List: ElectronicPublicationRoute("electronicPublication/types")
    data object Detail: ElectronicPublicationRoute("electronicPublication/detail")
}

fun routeForPubType(pubType: ElectronicPublicationType): String {
    return "${ElectronicPublicationRoute.List.name}/${pubType.typeId}"
}

fun NavGraphBuilder.electronicPublicationGraph(
    navController: NavController,
    bottomBarVisibility: (Boolean) -> Unit,
    openNavigationDrawer: () -> Unit
) {
    navigation(
        route = ElectronicPublicationRoute.Main.name,
        startDestination = ElectronicPublicationRoute.List.name
    ) {

        composable(
            route = ElectronicPublicationRoute.List.name,
            deepLinks = listOf( /* TODO */)
        ) {
            bottomBarVisibility(true)
            ElectronicPublicationsScreen(
                openDrawer = openNavigationDrawer,
                onPubTypeTap = { pubType ->
                    navController.navigate(routeForPubType(pubType))
                }
            )
        }

        composable(
            route = "${ElectronicPublicationRoute.List.name}/{pubType}",
            arguments = listOf(navArgument("pubType") { type = NavType.IntType })
        ) {
            bottomBarVisibility(true)
            ElectronicPublicationTypeBrowseScreen(
                onBack = { navController.popBackStack() },
                onBookmark = { publication ->
                    val key = BookmarkKey(publication.s3Key, DataSource.ELECTRONIC_PUBLICATION)
                    val encoded = Uri.encode(Json.encodeToString(key))
                    navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
                }
            )
        }

        composable(
            route = "${ElectronicPublicationRoute.Detail.name}?s3Key={s3Key}"
        ) { backstackEntry ->
            bottomBarVisibility(false)

            val s3Key = backstackEntry.arguments?.getString("s3Key")
            requireNotNull(s3Key) { "'s3Key' argument is required" }

            ElectronicPublicationDetailScreen(
                s3Key = Uri.decode(s3Key),
                onBack = { navController.popBackStack() },
                onBookmark = { publication ->
                    val key = BookmarkKey(publication.s3Key, DataSource.ELECTRONIC_PUBLICATION)
                    val encoded = Uri.encode(Json.encodeToString(key))
                    navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
                }
            )
        }
    }
}