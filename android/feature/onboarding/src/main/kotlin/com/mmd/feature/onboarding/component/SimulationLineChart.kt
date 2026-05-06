package com.mmd.feature.onboarding.component

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 자체 Canvas 라인 차트.
 * x축: 주차, y축: targetMaxReps. 12주 정도까지 가독성 보장.
 *
 * Phase 4(통계 화면)에서 차트 종류 늘어나면 Vico로 마이그레이션 검토.
 */
@Composable
internal fun SimulationLineChart(
    weeklyTargetReps: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
) {
    if (weeklyTargetReps.isEmpty()) return

    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = TextStyle(fontSize = 10.sp, color = onSurfaceVariant)
    val textMeasurer: TextMeasurer = rememberTextMeasurer()

    val maxY = (weeklyTargetReps.maxOfOrNull { it.second } ?: 10).coerceAtLeast(10)
    val minWeek = weeklyTargetReps.minOfOrNull { it.first } ?: 1
    val maxWeek = weeklyTargetReps.maxOfOrNull { it.first } ?: 1
    val weekRange = (maxWeek - minWeek).coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val padLeft = 28.dp.toPx()
        val padRight = 8.dp.toPx()
        val padTop = 8.dp.toPx()
        val padBottom = 24.dp.toPx()

        val chartW = size.width - padLeft - padRight
        val chartH = size.height - padTop - padBottom
        val originX = padLeft
        val originY = padTop + chartH

        // 축
        drawLine(outline, Offset(originX, padTop), Offset(originX, originY), 1.dp.toPx())
        drawLine(outline, Offset(originX, originY), Offset(originX + chartW, originY), 1.dp.toPx())

        // y-axis 라벨 (0, max/2, max)
        listOf(0, maxY / 2, maxY).forEach { value ->
            val y = originY - chartH * value / maxY.toFloat()
            val layout = textMeasurer.measure(value.toString(), labelStyle)
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(originX - layout.size.width - 4.dp.toPx(), y - layout.size.height / 2),
            )
        }

        // x-axis 라벨 (첫 주차 / 중간 / 마지막)
        val xLabels = listOf(minWeek, (minWeek + maxWeek) / 2, maxWeek).distinct()
        xLabels.forEach { week ->
            val x = originX + chartW * (week - minWeek) / weekRange.toFloat()
            val layout = textMeasurer.measure("${week}주", labelStyle)
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(x - layout.size.width / 2, originY + 4.dp.toPx()),
            )
        }

        // 데이터 포인트
        val points = weeklyTargetReps.map { (week, reps) ->
            val x = originX + chartW * (week - minWeek) / weekRange.toFloat()
            val y = originY - chartH * reps / maxY.toFloat()
            Offset(x, y)
        }

        // 라인
        for (i in 0 until points.size - 1) {
            drawLine(
                color = primary,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        // 점
        points.forEach { p ->
            drawCircle(color = primary, radius = 3.5.dp.toPx(), center = p)
        }
    }
}
