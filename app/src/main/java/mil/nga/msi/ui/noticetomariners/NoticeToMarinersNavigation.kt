package mil.nga.msi.ui.noticetomariners

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.os.BundleCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic
import mil.nga.msi.ui.bookmark.BookmarkRoute
import mil.nga.msi.ui.navigation.NavTypeNoticeToMarinersGraphic
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.noticetomariners.all.NoticeToMarinersAllScreen
import mil.nga.msi.ui.noticetomariners.corrections.NoticeToMarinersCorrectionsScreen
import mil.nga.msi.ui.noticetomariners.detail.NoticeToMarinersDetailScreen
import mil.nga.msi.ui.noticetomariners.detail.NoticeToMarinersGraphicScreen
import mil.nga.msi.ui.noticetomariners.query.NoticeToMarinersQueryScreen

sealed class NoticeToMarinersRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
): Route {
   data object Main: NoticeToMarinersRoute("ntms", "Notice To Mariners", "NTMs")
   data object Home: NoticeToMarinersRoute("ntms/home", "Notice To Mariners", "NTMs")
   data object All: NoticeToMarinersRoute("ntms/all", "Notice To Mariners", "NTMs")
   data object Query: NoticeToMarinersRoute("ntms/query", "Notice To Mariners", "NTMs")
   data object Corrections: NoticeToMarinersRoute("ntms/corrections", "Chart Corrections", "NTMs")
   data object Detail: NoticeToMarinersRoute("ntms/detail", "Notice To Mariners", "NTMs")
   data object Graphic: NoticeToMarinersRoute("ntms/graphic", "Notice To Mariners Graphic", "NTMs Graphic")
}

fun NavGraphBuilder.noticeToMarinersGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit
) {
   navigation(
      route = NoticeToMarinersRoute.Main.name,
      startDestination = NoticeToMarinersRoute.Home.name
   ) {
      composable(
         route = NoticeToMarinersRoute.Home.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${NoticeToMarinersRoute.Home.name}" })
      ) {
         bottomBarVisibility(true)

         NoticeToMarinersHomeScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { type ->
               when (type) {
                  NoticeToMarinersHomeChoice.ALL -> {
                     navController.navigate(NoticeToMarinersRoute.All.name)
                  }
                  NoticeToMarinersHomeChoice.QUERY -> {
                     navController.navigate(NoticeToMarinersRoute.Query.name)
                  }
               }
            }
         )
      }

      composable(NoticeToMarinersRoute.Query.name) {
         bottomBarVisibility(false)

         NoticeToMarinersQueryScreen(
            close = { navController.popBackStack() },
            onQuery = {
               navController.navigate(NoticeToMarinersRoute.Corrections.name)
            }
         )
      }

      composable(NoticeToMarinersRoute.Corrections.name) {
         bottomBarVisibility(false)

         NoticeToMarinersCorrectionsScreen(
            onNoticeTap = {
               navController.navigate("${NoticeToMarinersRoute.Detail.name}?noticeNumber=${it}")
            },
            close = { navController.popBackStack() }
         )
      }

      composable(
         route = NoticeToMarinersRoute.All.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${NoticeToMarinersRoute.All.name}" })
      ) {
         bottomBarVisibility(false)

         NoticeToMarinersAllScreen(
            close = { navController.popBackStack() },
            onTap = {
               navController.navigate("${NoticeToMarinersRoute.Detail.name}?noticeNumber=${it}")
            },
            onBookmark = { noticeNumber ->
               val key = BookmarkKey(noticeNumber.toString(), DataSource.NOTICE_TO_MARINERS)
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
            }
         )
      }

      composable("${NoticeToMarinersRoute.Detail.name}?noticeNumber={noticeNumber}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("noticeNumber")?.let { noticeNumber ->
            NoticeToMarinersDetailScreen(
               noticeNumber.toIntOrNull(),
               close = { navController.popBackStack() },
               onGraphicTap = { graphic ->
                  val encoded = Uri.encode(Json.encodeToString(graphic))
                  navController.navigate( "${NoticeToMarinersRoute.Graphic.name}?graphic=$encoded")
               },
               onBookmark = {
                  val key = BookmarkKey(it.toString(), DataSource.NOTICE_TO_MARINERS)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
               }
            )
         }
      }

      composable(
         route = "${NoticeToMarinersRoute.Graphic.name}?graphic={graphic}",
         arguments = listOf(navArgument("graphic") { type = NavType.NavTypeNoticeToMarinersGraphic })
      ) { backstackEntry ->
         bottomBarVisibility(false)
         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "graphic", NoticeToMarinersGraphic::class.java)
         }?.let { graphic ->
            NoticeToMarinersGraphicScreen(
               graphic = graphic,
               close = { navController.popBackStack() }
            )
         }
      }
   }
}