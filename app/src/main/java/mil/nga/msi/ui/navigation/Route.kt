package mil.nga.msi.ui.navigation

import androidx.compose.ui.graphics.Color

interface Route {
   val name: String
   val title: String
   val shortTitle: String
   val color: Color
}