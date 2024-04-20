package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> DropdownMenu(
    options: List<T>,
    optionFormatter: (T?) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    selectedOption: T?,
    selectionChanged: (T) -> Unit,
    label: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        val value: String = optionFormatter(selectedOption)
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = label?.let { { Text(text = it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectionChanged(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(optionFormatter(selectionOption))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SuggestionMenu(
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
