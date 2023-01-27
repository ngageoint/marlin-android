package mil.nga.msi.ui.noticetomariners

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.noticetomariners.all.NoticeToMarinersAllScreen
import mil.nga.msi.ui.noticetomariners.detail.NoticeToMarinersDetailScreen
import mil.nga.msi.ui.noticetomariners.detail.NoticeToMarinersGraphicScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class NoticeToMarinersRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.ASAM.color
): Route {
   object Main: NoticeToMarinersRoute("ntms", "Notice To Mariners", "NTMs")
   object Home: NoticeToMarinersRoute("ntms/home", "Notice To Mariners", "NTMs")
   object All: NoticeToMarinersRoute("ntms/all", "Notice To Mariners", "NTMs")
   object Detail: NoticeToMarinersRoute("ntms/detail", "Notice To Mariners", "NTMs")
   object Graphic: NoticeToMarinersRoute("ntms/graphic", "Notice To Mariners Graphic", "NTMs Graphic")
}

fun NavGraphBuilder.noticeToMarinersGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   shareGraphic: (String) -> Unit
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
                  NoticeToMarinersHomeChoice.CHART_CORRECTIONS -> {}
               }
            }
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
                  val url = URLEncoder.encode(graphic.url, StandardCharsets.UTF_8.toString())
                  navController.navigate("${NoticeToMarinersRoute.Graphic.name}?title=${graphic.title}&url=$url")
               }
            )
         }
      }

      composable("${NoticeToMarinersRoute.Graphic.name}?title={title}&url={url}") { backstackEntry ->
         bottomBarVisibility(false)

         val title: String = checkNotNull(backstackEntry.arguments?.getString("title"))
         val url: String = checkNotNull(backstackEntry.arguments?.getString("url"))

         NoticeToMarinersGraphicScreen(
            title = title,
            url = url,
            onShare = { shareGraphic(url) },
            close = { navController.popBackStack() },
         )
      }
   }
}