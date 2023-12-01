package mil.nga.msi.ui.route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mil.nga.msi.datasource.route.RouteWithWaypoints
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RouteSummary(
    route: RouteWithWaypoints,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        route.route.name?.let { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text("This many waypoints " + route.waypoints.size)
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Text(
                text = "Created " + dateFormat.format(route.route.createdTime),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val header = listOfNotNull(route.route.name)



//        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
//            asam.description?.let {
//                Text(
//                    text = it,
//                    maxLines = 3,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//
//            BookmarkNotes(
//                notes = bookmark?.notes,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//        }
    }
}