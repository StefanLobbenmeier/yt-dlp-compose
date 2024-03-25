package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp

@Composable
fun <T> DropdownMenu(
    options: List<T>,
    optionBuilder: @Composable (T) -> Unit = { Text(it.toString()) },
    modifier: Modifier = Modifier,
    selectedOption: T?,
    selectionChanged: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown

    Column(modifier = modifier) {
        Card(
            modifier =
                Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
        ) {
            DropdownMenuItem(onClick = { expanded = !expanded }) {
                if (selectedOption != null) {
                    optionBuilder(selectedOption)
                }
                Spacer(Modifier.weight(1f))
                Icon(icon, "Drop Down Icon", modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = !expanded
                            selectionChanged(option)
                        }) {
                            optionBuilder(option)
                        }
                }
            }
    }
}
