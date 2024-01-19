package mil.nga.msi.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mil.nga.msi.datasource.route.RouteWithWaypoints
import mil.nga.msi.ui.datasource.DataSourceIcon
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RouteSummary(
    route: RouteWithWaypoints,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        route.route.name?.let { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Text(
                text = "Created " + dateFormat.format(route.route.createdTime),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val first = route.getSortedWaypoints().first()
        val last = route.getSortedWaypoints().last()

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DataSourceIcon(
                dataSource = first.dataSource,
                iconSize = 24
            )
            val (title, latLng) = first.getTitleAndCoordinate()
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                Icons.Rounded.MoreHoriz,
                contentDescription = "more"
            )
            DataSourceIcon(
                dataSource = last.dataSource,
                iconSize = 24
            )
            val (title2, latLng2) = last.getTitleAndCoordinate()
            Text(
                text = title2,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            route.route.distanceNauticalMiles()?.let {
                Text(
                    text = "Total Distance: ${"%.2f".format(it)} nmi",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}