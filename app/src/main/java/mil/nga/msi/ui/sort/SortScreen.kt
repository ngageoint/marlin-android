package mil.nga.msi.ui.sort

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.sort.SortDirection
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.add
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.remove
import mil.nga.msi.ui.theme.screenBackground

enum class SortField {
   PRIMARY, SECONDARY
}

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

   Column {
      TopBar(
         title = title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         Sort(
            section = section,
            options = options,
            parameters = parameters,
            addSort = { field, parameter ->
               when (field) {
                  SortField.PRIMARY -> viewModel.addPrimarySort(dataSource, parameter)
                  SortField.SECONDARY -> viewModel.addSecondarySort(dataSource, parameter)
               }
            },
            removeSort = { field ->
               when (field) {
                  SortField.PRIMARY -> viewModel.removePrimarySort(dataSource)
                  SortField.SECONDARY -> viewModel.removeSecondarySort(dataSource)
               }
            },
            resetSort = {
               viewModel.reset(dataSource)
            },
            setSection = {
               viewModel.setSection(dataSource, it)
            }
         )
      }
   }
}

@Composable
private fun Sort(
   section: Boolean,
   options: List<FilterParameter>,
   parameters: List<SortParameter>,
   addSort: (SortField, SortParameter) -> Unit,
   removeSort: (SortField) -> Unit,
   resetSort: () -> Unit,
   setSection: (Boolean) -> Unit
) {
   val scrollState = rememberScrollState()
   val primarySort = parameters.getOrNull(0)
   val secondarySort = parameters.getOrNull(1)

   Column(
      Modifier
         .fillMaxSize()
         .verticalScroll(scrollState)
         .padding(bottom = 16.dp)
   ) {
      if (options.isNotEmpty()) {
         SortField(
            title = "Primary Sort Field",
            parameter = primarySort,
            sortOptions = options,
            addSort = {
               addSort(SortField.PRIMARY, it)
            },
            removeSort = {
               removeSort(SortField.PRIMARY)
            }
         )

         Column(Modifier.animateContentSize()) {
            if (parameters.isNotEmpty()) {
               Divider(Modifier.fillMaxWidth())

               SortField(
                  title = "Secondary Sort Field",
                  parameter = secondarySort,
                  sortOptions = options,
                  addSort = {
                     addSort(SortField.SECONDARY, it)
                  },
                  removeSort = {
                     removeSort(SortField.SECONDARY)
                  }
               )
            }
         }

         Reset(
            onReset = {
               resetSort()
            }
         )

         Section(
            section = section,
            onSection = {
               setSection(it)
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
      TextButton(
         onClick = { onReset() },
         colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.tertiary
         )
      ) {
         Text("Reset to Default")
      }
   }
}

@Composable
private fun Section(
   section: Boolean,
   onSection: (Boolean) -> Unit,
) {
   Surface(
      modifier = Modifier.fillMaxWidth()
   ) {
      Row(
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(text = "Group by primary sort field")
         }

         Switch(
            checked = section,
            onCheckedChange = onSection
         )
      }
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
   Surface {
      Column(Modifier.padding(16.dp)) {
         if (parameter == null) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = title,
                  style = MaterialTheme.typography.titleSmall
               )
            }

            SortPicker(
               sortParameters = sortOptions,
               addSort = addSort
            )
         } else {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = title,
                  style = MaterialTheme.typography.titleSmall,
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
            tint = MaterialTheme.colorScheme.add,
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
            style = MaterialTheme.typography.titleMedium
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
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
               },
               text = {
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
            )
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
            style = MaterialTheme.typography.titleMedium
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
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
               },
               text = {
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
            )
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
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(vertical = 8.dp)
      )

      IconButton(
         onClick = { removeSort() }
      ) {
         Icon(
            imageVector = Icons.Filled.RemoveCircle,
            tint = MaterialTheme.colorScheme.remove,
            contentDescription = "Remove Sort",
         )
      }
   }
}

