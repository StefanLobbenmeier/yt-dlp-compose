package de.lobbenmeier.stefan.common.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// https://composeicons.com/icons/material-symbols/outlined/subtitles_off
val SubtitlesOff: ImageVector
    get() {
        if (_SubtitlesOff != null) {
            return _SubtitlesOff!!
        }
        _SubtitlesOff =
            ImageVector.Builder(
                    name = "SubtitlesOff",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f
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
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(822f, 934f)
                        lineTo(686f, 800f)
                        horizontalLineTo(160f)
                        quadToRelative(-33f, 0f, -56.5f, -23.5f)
                        reflectiveQuadTo(80f, 720f)
                        verticalLineToRelative(-480f)
                        quadToRelative(0f, -33f, 23.5f, -56.5f)
                        reflectiveQuadTo(160f, 160f)
                        lineToRelative(80f, 80f)
                        horizontalLineToRelative(-80f)
                        verticalLineToRelative(480f)
                        horizontalLineToRelative(446f)
                        lineToRelative(-80f, -80f)
                        horizontalLineTo(240f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(206f)
                        lineTo(26f, 138f)
                        lineToRelative(56f, -56f)
                        lineTo(878f, 878f)
                        close()
                        moveToRelative(48f, -178f)
                        lineToRelative(-70f, -70f)
                        verticalLineToRelative(-446f)
                        horizontalLineTo(354f)
                        lineToRelative(-80f, -80f)
                        horizontalLineToRelative(526f)
                        quadToRelative(33f, 0f, 56.5f, 23.5f)
                        reflectiveQuadTo(880f, 240f)
                        verticalLineToRelative(476f)
                        quadToRelative(0f, 11f, -2f, 21f)
                        reflectiveQuadToRelative(-8f, 19f)
                        moveTo(594f, 480f)
                        lineToRelative(-80f, -80f)
                        horizontalLineToRelative(206f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(-354f, 0f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(143f, 17f)
                    }
                }
                .build()
        return _SubtitlesOff!!
    }

private var _SubtitlesOff: ImageVector? = null
