package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun Thumbnail(thumbnail: String?) {
    val modifier = Modifier.aspectRatio(16f / 9f)

    if (thumbnail != null) {
        val painterResource = lazyPainterResource(data = thumbnail)
        KamelImage(
            resource = painterResource,
            contentDescription = "Profile",
            modifier = modifier,
            onLoading = { progress -> ProgressIndicator(modifier, progress) }
        )
    } else {
        ProgressIndicator(modifier)
    }
}

@Composable
private fun ProgressIndicator(modifier: Modifier, progress: Float? = null) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (progress == null || progress < 0.3f) {
            CircularProgressIndicator()
        } else {
            CircularProgressIndicator(progress)
        }
    }
}
