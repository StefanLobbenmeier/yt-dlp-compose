package de.lobbenmeier.stefan.common.ui

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DesktopVerticalScrollbar(adapter: ScrollbarAdapter, modifier: Modifier = Modifier) {
    VerticalScrollbar(
        adapter = adapter,
        modifier = modifier,
        style =
            ScrollbarStyle(
                minimalHeight = 24.dp,
                thickness = 8.dp,
                shape = RoundedCornerShape(4.dp),
                hoverDurationMillis = 150,
                unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.35f),
                hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            ),
    )
}

@Composable
fun DesktopScrollableColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = rememberScrollState()
    Box(modifier) {
        Column(
            Modifier.fillMaxSize().padding(end = 12.dp).verticalScroll(state),
            verticalArrangement = verticalArrangement,
            content = content,
        )
        if (state.canScrollBackward || state.canScrollForward) {
            DesktopVerticalScrollbar(
                rememberScrollbarAdapter(state),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            )
        }
    }
}

@Composable
fun DesktopLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    val state = rememberLazyListState()
    Box(modifier) {
        LazyColumn(
            Modifier.fillMaxSize().padding(end = 12.dp),
            state = state,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content,
        )
        if (state.canScrollBackward || state.canScrollForward) {
            DesktopVerticalScrollbar(
                rememberScrollbarAdapter(state),
                Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            )
        }
    }
}
