package com.mmd.feature.tracker.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mmd.core.design.theme.GrassDark0
import com.mmd.core.design.theme.GrassDark1
import com.mmd.core.design.theme.GrassDark2
import com.mmd.core.design.theme.GrassDark3
import com.mmd.core.design.theme.GrassDark4
import com.mmd.core.design.theme.GrassLight0
import com.mmd.core.design.theme.GrassLight1
import com.mmd.core.design.theme.GrassLight2
import com.mmd.core.design.theme.GrassLight3
import com.mmd.core.design.theme.GrassLight4
import com.mmd.core.domain.model.GrassCell
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * 4주(또는 weekCount주) × 7요일 잔디 그리드.
 * 각 셀은 12dp + 2dp gap, 색상은 intensity 0~4로 매핑.
 */
@Composable
internal fun MiniGrassGrid(
    cells: List<GrassCell>,
    modifier: Modifier = Modifier,
    weekCount: Int = 4,
    today: LocalDate = LocalDate.now(),
) {
    val cellsByDate = cells.associateBy { it.date }

    val gridEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    val gridStart = gridEnd.minusDays((weekCount * 7L - 1L))

    val palette = grassPalette()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        DayOfWeek.entries.forEach { day ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = dayLabel(day),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(20.dp),
                )
                repeat(weekCount) { weekIdx ->
                    val date = gridStart.plusDays((weekIdx * 7 + (day.value - 1)).toLong())
                    val intensity = cellsByDate[date]?.intensityLevel?.coerceIn(0, 4) ?: 0
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(palette[intensity]),
                    )
                }
            }
        }
    }
}

@Composable
private fun grassPalette(): List<Color> = if (isSystemInDarkTheme()) {
    listOf(GrassDark0, GrassDark1, GrassDark2, GrassDark3, GrassDark4)
} else {
    listOf(GrassLight0, GrassLight1, GrassLight2, GrassLight3, GrassLight4)
}

private fun dayLabel(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}
