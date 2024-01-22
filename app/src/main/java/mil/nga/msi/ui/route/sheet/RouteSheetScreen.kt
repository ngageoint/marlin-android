package mil.nga.msi.ui.route.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.route.RouteSummary
import mil.nga.msi.ui.route.list.RouteViewModel

@Composable
fun RouteSheetScreen(
    routeId: Long,
    modifier: Modifier = Modifier,
    onDetails: (() -> Unit)? = null,
    viewModel: RouteViewModel = hiltViewModel()
) {
    viewModel.setRouteId(routeId)
    val route by viewModel.route.observeAsState()

    Column(modifier = modifier) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            DataSourceIcon(
                dataSource = DataSource.ROUTE,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            route?.let { routeWithWaypoints ->
                RouteSummary(route = routeWithWaypoints)
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                onDetails?.let {
                    TextButton(
                        onClick = onDetails
                    ) {
                        Text("MORE DETAILS")
                    }
                }
            }
        }
    }
}