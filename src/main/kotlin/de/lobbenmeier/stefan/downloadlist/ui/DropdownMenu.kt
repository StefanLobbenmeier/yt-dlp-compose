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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

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
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier =
            modifier.semantics {
                role = Role.DropdownList
                contentDescription = label
            },
    ) {
        val value: String = optionFormatter(selectedOption)
        OutlinedTextField(
            modifier = textFieldModifier,
            readOnly = onTextInput == null,
            value = value,
            label = { Text(text = label) },
            singleLine = true,
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
