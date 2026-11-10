package com.sam.qrforge.presentation.common.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIconVectors {

    val backArrow: ImageVector
        get() {
            if (_arrow_back != null) {
                return _arrow_back!!
            }
            _arrow_back =
                ImageVector.Builder(
                    name = "arrow_back",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.Companion.NonZero,
                        ) {
                            moveTo(7.83f, 13f)
                            lineToRelative(5.6f, 5.6f)
                            lineTo(12f, 20f)
                            lineTo(4f, 12f)
                            lineTo(12f, 4f)
                            lineToRelative(1.43f, 1.4f)
                            lineTo(7.83f, 11f)
                            horizontalLineTo(20f)
                            verticalLineToRelative(2f)
                            horizontalLineTo(7.83f)
                            close()
                        }
                    }
                    .build()
            return _arrow_back!!
        }


    val check: ImageVector
        get() {
            if (_check != null) {
                return _check!!
            }
            _check =
                ImageVector.Builder(
                    name = "check",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                    .apply {
                        path(
                            fill = SolidColor(Color.Black),
                            fillAlpha = 1f,
                            stroke = null,
                            strokeAlpha = 1f,
                            strokeLineWidth = 1f,
                            strokeLineCap = StrokeCap.Butt,
                            strokeLineJoin = StrokeJoin.Bevel,
                            strokeLineMiter = 1f,
                            pathFillType = PathFillType.Companion.NonZero,
                        ) {
                            moveTo(9.55f, 18f)
                            lineTo(3.85f, 12.3f)
                            lineTo(5.28f, 10.88f)
                            lineToRelative(4.28f, 4.28f)
                            lineTo(18.73f, 5.97f)
                            lineTo(20.15f, 7.4f)
                            lineTo(9.55f, 18f)
                            close()
                        }
                    }
                    .build()
            return _check!!
        }

    private var _check: ImageVector? = null
    private var _arrow_back: ImageVector? = null
}
