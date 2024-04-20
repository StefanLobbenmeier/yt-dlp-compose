package de.lobbenmeier.stefan.downloadlist.ui

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> DropdownMenu(
    options: List<T>,
    optionFormatter: (T?) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    selectedOption: T?,
    selectionChanged: (T) -> Unit,
    onTextInput: ((String) -> Unit)? = null,
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
            modifier = textFieldModifier,
            readOnly = onTextInput == null,
            value = value,
            label = label?.let { { Text(text = it) } },
            onValueChange = onTextInput ?: {},
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
