package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    optionBuilder: (T) -> String = { it.toString() },
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
            DropdownMenuItem(
                onClick = { expanded = !expanded },
                text = {
                    if (selectedOption != null) {
                        Text(optionBuilder(selectedOption))
                    }

                    Spacer(Modifier.weight(1f))
                    Icon(icon, "Drop Down Icon", modifier = Modifier.size(16.dp))
                })
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
                        },
                        text = { Text(optionBuilder(option)) })
                }
            }
    }
}
