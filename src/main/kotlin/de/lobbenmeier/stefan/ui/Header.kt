package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Picture

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun Header() {
    var downloadUrl by remember { mutableStateOf("") }

    TopAppBar (
        contentPadding = PaddingValues(20.dp),
        backgroundColor = MaterialTheme.colors.surface,

        ){
        OutlinedTextField(
            downloadUrl,
            singleLine = true,
            onValueChange = { downloadUrl = it },
            placeholder = { Text("Enter a video URL") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                IconButton(onClick = {

                }) {
                    Icon(Icons.Default.Add, "Download")
                }
            }
        )

        Spacer(Modifier.weight(0.05f))

        IconButton(onClick = {

        }) {
            Icon(Icons.Default.Settings, "Settings")
        }
    }



}