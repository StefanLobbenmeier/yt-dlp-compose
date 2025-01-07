package de.lobbenmeier.stefan.common.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// https://composeicons.com/icons/material-symbols/outlined/subtitles
val Subtitles: ImageVector
    get() {
        if (_Subtitles != null) {
            return _Subtitles!!
        }
        _Subtitles =
            ImageVector.Builder(
                    name = "Subtitles",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(240f, 640f)
                        horizontalLineToRelative(320f)
                        verticalLineToRelative(-80f)
                        horizontalLineTo(240f)
                        close()
                        moveToRelative(400f, 0f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(-80f)
                        close()
                        moveTo(240f, 480f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(-80f)
                        close()
                        moveToRelative(160f, 0f)
                        horizontalLineToRelative(320f)
                        verticalLineToRelative(-80f)
                        horizontalLineTo(400f)
                        close()
                        moveTo(160f, 800f)
                        quadToRelative(-33f, 0f, -56.5f, -23.5f)
                        reflectiveQuadTo(80f, 720f)
                        verticalLineToRelative(-480f)
                        quadToRelative(0f, -33f, 23.5f, -56.5f)
                        reflectiveQuadTo(160f, 160f)
                        horizontalLineToRelative(640f)
                        quadToRelative(33f, 0f, 56.5f, 23.5f)
                        reflectiveQuadTo(880f, 240f)
                        verticalLineToRelative(480f)
                        quadToRelative(0f, 33f, -23.5f, 56.5f)
                        reflectiveQuadTo(800f, 800f)
                        close()
                        moveToRelative(0f, -80f)
                        horizontalLineToRelative(640f)
                        verticalLineToRelative(-480f)
                        horizontalLineTo(160f)
                        close()
                        moveToRelative(0f, 0f)
                        verticalLineToRelative(-480f)
                        close()
                    }
                }
                .build()
        return _Subtitles!!
    }

private var _Subtitles: ImageVector? = null
