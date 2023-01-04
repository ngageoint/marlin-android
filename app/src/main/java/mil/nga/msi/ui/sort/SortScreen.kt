package mil.nga.msi.ui.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.sort.SortDirection
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.add
import mil.nga.msi.ui.theme.remove
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun SortScreen(
   dataSource: DataSource,
   close: () -> Unit,
   viewModel: SortViewModel = hiltViewModel()
) {
   viewModel.setDataSource(dataSource)
   val title by viewModel.title.observeAsState("")
   val section by viewModel.section.observeAsState(false)
   val parameters by viewModel.sortParameters.observeAsState(emptyList())
   val options by viewModel.sortOptions.observeAsState(emptyList())

   Column(
      Modifier
         .fillMaxSize()
         .background(MaterialTheme.colors.screenBackground)
   ) {
      TopBar(
         title = title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      val primarySort = parameters.getOrNull(0)
      val secondarySort = parameters.getOrNull(1)

      if (options.isNotEmpty()) {
         SortField(
            title = "Primary Sort Field",
            parameter = primarySort,
            sortOptions = options,
            addSort = {
               viewModel.addPrimarySort(dataSource, it)
            },
            removeSort = {
               viewModel.removePrimarySort(dataSource)
            }
         )

         Divider(Modifier.fillMaxWidth())

         SortField(
            title = "Secondary Sort Field",
            parameter = secondarySort,
            sortOptions = options,
            addSort = {
               viewModel.addSecondarySort(dataSource, it)
            },
            removeSort = {
               viewModel.removeSecondarySort(dataSource)
            }
         )

         Reset(
            onReset = {
               viewModel.reset(dataSource)
            }
         )

         Section(
            section = section,
            onSection = {
               viewModel.setSection(dataSource, it)
            }
         )
      }
   }
}

@Composable fun Reset(
   onReset: () -> Unit
) {
   Row(
      horizontalArrangement = Arrangement.End,
      modifier = Modifier
         .fillMaxWidth()
         .padding(16.dp)
   ) {
      TextButton(onClick = { onReset() }) {
         Text("Reset to Default")
      }
   }
}

@Composable
private fun Section(
   section: Boolean,
   onSection: (Boolean) -> Unit,
) {
   Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .background(MaterialTheme.colors.background)
         .padding(16.dp)
   ) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
         Text(text = "Group by primary sort field")
      }

      Switch(
         checked = section,
         onCheckedChange = onSection
      )
   }
}

@Composable
private fun SortField(
   title: String,
   parameter: SortParameter?,
   sortOptions: List<FilterParameter>,
   addSort: (SortParameter) -> Unit,
   removeSort: (SortParameter) -> Unit
) {
   Column(
      Modifier
         .background(MaterialTheme.colors.background)
         .padding(16.dp)
   ) {
      if (parameter == null) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = title,
               style = MaterialTheme.typography.subtitle1
            )
         }

         SortPicker(
            sortParameters = sortOptions,
            addSort = addSort
         )
      } else {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = title,
               style = MaterialTheme.typography.subtitle1,
            )
         }

         SortValue(
            parameter = parameter,
            removeSort = {
               removeSort(parameter)
            }
         )
      }
   }
}

@Composable
private fun SortPicker(
   sortParameters: List<FilterParameter>,
   addSort: (SortParameter) -> Unit
) {
   var parameter by remember { mutableStateOf(sortParameters.first()) }
   var direction by remember { mutableStateOf(SortDirection.values().first()) }

   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
   ) {
      Column(
         Modifier
            .weight(1f)
            .fillMaxWidth()
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
         ) {
            ParameterSelection(
               sortParameters = sortParameters,
               selectedParameter = parameter,
               onSelectParameter = {
                  parameter = it
               },
               modifier = Modifier.padding(end = 16.dp)
            )

            DirectionSelection(
               selectedDirection = direction,
               onSelectDirection = { direction = it }
            )
         }
      }

      IconButton(
         onClick = {
            addSort(
               SortParameter(
                  parameter = parameter,
                  direction = direction,
               )
            )
         }
      ) {
         Icon(
            imageVector = Icons.Filled.AddCircle,
            tint = MaterialTheme.colors.add,
            contentDescription = "Add Sort",
         )
      }
   }
}

@Composable
private fun ParameterSelection(
   sortParameters: List<FilterParameter>,
   selectedParameter: FilterParameter,
   onSelectParameter: (FilterParameter) -> Unit,
   modifier: Modifier = Modifier,
) {
   var expanded by remember { mutableStateOf(false) }

   Column(modifier = modifier) {
      Row(
         modifier = Modifier.clickable {
            expanded = true
         }
      ) {
         Text(
            text = selectedParameter.title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primary
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Parameters"
         )
      }

      DropdownMenu(
         expanded = expanded,
         onDismissRequest = { expanded = false }
      ) {
         sortParameters.forEach { parameter ->
            DropdownMenuItem(
               onClick = {
                  onSelectParameter(parameter)
                  expanded = false
               }
            ) {
               Row(
                  verticalAlignment = Alignment.CenterVertically
               ) {
                  if (selectedParameter == parameter) {
                     Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected Parameter"
                     )
                  } else {
                     Spacer(modifier = Modifier.size(24.dp))
                  }

                  Text(
                     text = parameter.title,
                     modifier = Modifier.padding(start = 8.dp)
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun DirectionSelection(
   selectedDirection: SortDirection,
   onSelectDirection: (SortDirection) -> Unit,
   modifier: Modifier = Modifier,
) {
   var expanded by remember { mutableStateOf(false) }

   Column(modifier = modifier) {
      Row(
         modifier = Modifier.clickable {
            expanded = true
         }
      ) {
         Text(
            text = selectedDirection.title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primary
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Directions"
         )
      }

      DropdownMenu(
         expanded = expanded,
         onDismissRequest = { expanded = false }
      ) {
         SortDirection.values().forEach { direction ->
            DropdownMenuItem(
               onClick = {
                  onSelectDirection(direction)
                  expanded = false
               }
            ) {
               Row {
                  if (selectedDirection == direction) {
                     Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected Direction"
                     )
                  } else {
                     Spacer(modifier = Modifier.size(24.dp))
                  }

                  Text(
                     text = direction.title,
                     modifier = Modifier.padding(start = 8.dp)
                  )
               }
            }
         }
      }
   }
}

@Composable
fun SortValue(
   parameter: SortParameter,
   removeSort: () -> Unit
) {
   Row (
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
   ) {
      val text = "${parameter.parameter.title} ${parameter.direction.title}"
      Text(
         text = text,
         style = MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(vertical = 8.dp)
      )

      IconButton(
         onClick = { removeSort() }
      ) {
         Icon(
            imageVector = Icons.Filled.RemoveCircle,
            tint = MaterialTheme.colors.remove,
            contentDescription = "Remove Sort",
         )
      }
   }
}

