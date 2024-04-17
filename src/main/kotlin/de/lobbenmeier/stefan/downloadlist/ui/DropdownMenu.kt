package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                        selectionChanged(option)
                    }
                ) {
                    optionBuilder(option)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Menu(
    options: List<String>,
    optionBuilder: @Composable (String) -> Unit = { Text(it) },
    modifier: Modifier = Modifier,
    selectedOption: String,
    selectionChanged: (String) -> Unit,
    label: String,
    width: Dp? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = selectionChanged,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = width?.let { Modifier.width(it) } ?: Modifier
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectionChanged(selectionOption)
                        expanded = false
                    }
                ) {
                    optionBuilder(selectionOption)
                }
            }
        }
    }
}
