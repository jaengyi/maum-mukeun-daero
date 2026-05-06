package com.mmd.core.simulation

import java.time.DayOfWeek
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * 08 문서의 알고리즘 구현. 순수 deterministic — Random 사용 안 함.
 * adjustPlan(BURNOUT/OVER_PERFORM 등)은 Phase 5에서 추가.
 */
class PullupSimulatorImpl : PullupSimulator {

    override fun generatePlan(input: SimulationInput): SimulationResult {
        val currentScore = computeStrengthScore(input)
        val baseGain = baseGainFor(input.intensityPreference)
        val freqMul = frequencyMultiplier(input.availableDaysOfWeek.size)
        val weeklyGain = baseGain * freqMul

        val totalWeeks = computeTotalWeeks(currentScore, weeklyGain)
        val (foundationEnd, buildEnd) = phaseBoundaries(totalWeeks, currentScore)

        val plans = (1..totalWeeks).map { week ->
            val phase = phaseFor(week, foundationEnd, buildEnd)
            val targetMaxReps = maxRepsFromScore(currentScore + weeklyGain * week)
            val executions = composeWeekExecutions(phase, targetMaxReps, input.currentMaxPullups)
            val totalVolume = computeTotalVolume(executions)
            val dailyTasks = distributeDailyTasks(input.availableDaysOfWeek, executions)

            WeeklyPlanDraft(
                weekNumber = week,
                phase = phase,
                targetMaxReps = targetMaxReps,
                totalVolume = totalVolume,
                dailyTasks = dailyTasks,
            )
        }

        val milestones = computeMilestones(plans)
        val notes = buildNotes(input, totalWeeks)

        return SimulationResult(totalWeeks, plans, milestones, notes)
    }

    // ----- 능력 점수 (08 §2.1) -----

    private fun computeStrengthScore(input: SimulationInput): Float {
        val baseFromMaxReps = baseScoreForMaxReps(input.currentMaxPullups)
        val deadHangBonus = (input.currentDeadHangSeconds / 4).coerceAtMost(15).toFloat()
        val bmi = input.weightKg / (input.heightCm / 100f).pow(2)
        val bodyLoadPenalty = if (bmi > 27f) ((bmi - 27f) * 2f).coerceAtMost(10f) else 0f
        return baseFromMaxReps + deadHangBonus - bodyLoadPenalty
    }

    private fun baseScoreForMaxReps(maxReps: Int): Float {
        // 0=0, 1=15, 2=25, 3=35, 5=50, 7=65, 10=80, 15=95 (선형 보간)
        if (maxReps <= 0) return 0f
        if (maxReps >= 15) return 95f
        val anchors = listOf(0 to 0f, 1 to 15f, 2 to 25f, 3 to 35f, 5 to 50f, 7 to 65f, 10 to 80f, 15 to 95f)
        for (i in 0 until anchors.size - 1) {
            val (lr, ls) = anchors[i]
            val (hr, hs) = anchors[i + 1]
            if (maxReps in lr..hr) {
                if (lr == hr) return ls
                val frac = (maxReps - lr).toFloat() / (hr - lr).toFloat()
                return ls + (hs - ls) * frac
            }
        }
        return 95f
    }

    // ----- 주간 게인 / 총 주차 (08 §2.3, §2.4) -----

    private fun baseGainFor(pref: IntensityPreference): Float = when (pref) {
        IntensityPreference.GENTLE -> 4f
        IntensityPreference.NORMAL -> 6f
        IntensityPreference.AGGRESSIVE -> 8f
    }

    private fun frequencyMultiplier(numDays: Int): Float = when (numDays) {
        1 -> 0.4f
        2 -> 0.7f
        3 -> 1.0f
        4 -> 1.15f
        else -> 1.2f
    }

    private fun computeTotalWeeks(currentScore: Float, weeklyGain: Float): Int {
        val gap = (80f - currentScore).coerceAtLeast(8f)
        return ((gap / weeklyGain).toInt() + 1).coerceIn(6, 16)
    }

    // ----- 페이즈 (08 §2.5) -----
    // 고능력 사용자(score >= 50)는 FOUNDATION 짧고 PEAK 비중 크게.

    private fun phaseBoundaries(totalWeeks: Int, currentScore: Float): Pair<Int, Int> {
        val (foundationRatio, peakRatio) = when {
            currentScore >= 65f -> 0.0 to 0.5
            currentScore >= 50f -> 0.1 to 0.35
            else -> 0.25 to 0.25
        }
        val foundationEnd = (totalWeeks * foundationRatio).toInt()
        val peakLength = (totalWeeks * peakRatio).toInt().coerceAtLeast(1)
        val peakStart = totalWeeks - peakLength + 1
        val buildEnd = (peakStart - 1)
            .coerceAtLeast(foundationEnd + 1)
            .coerceAtMost(totalWeeks - 1)
        return foundationEnd to buildEnd
    }

    private fun phaseFor(week: Int, foundationEnd: Int, buildEnd: Int): TrainingPhase = when {
        week <= foundationEnd -> TrainingPhase.FOUNDATION
        week <= buildEnd -> TrainingPhase.BUILD
        else -> TrainingPhase.PEAK
    }

    // ----- 주차별 능력치 (08 §5.1) -----

    private fun maxRepsFromScore(score: Float): Int = when {
        score < 15f -> 0
        score < 25f -> 1
        score < 35f -> 2
        score < 50f -> 3
        score < 65f -> 5
        score < 80f -> {
            // 65 → 7, 80 → 9 선형 보간
            val frac = (score - 65f) / 15f
            (7f + frac * 2f).roundToInt().coerceIn(7, 9)
        }
        else -> 10
    }

    // ----- 주간 종목 구성 (08 §4) -----

    private fun composeWeekExecutions(
        phase: TrainingPhase,
        targetMaxReps: Int,
        currentMaxPullups: Int,
    ): List<TaskExecutionDraft> = when (phase) {
        TrainingPhase.FOUNDATION -> listOf(
            TaskExecutionDraft(ExerciseType.DEAD_HANG, targetSets = 3, targetReps = 30, restSeconds = 60),
            TaskExecutionDraft(ExerciseType.AUSTRALIAN_PULLUP, targetSets = 3, targetReps = 8, restSeconds = 90),
            TaskExecutionDraft(ExerciseType.ASSISTED_PULLUP, targetSets = 3, targetReps = 5, restSeconds = 90),
        )

        TrainingPhase.BUILD -> buildList {
            add(TaskExecutionDraft(ExerciseType.NEGATIVE, 3, 5, 120))
            add(TaskExecutionDraft(ExerciseType.ASSISTED_PULLUP, 2, 6, 90))
            // T10 안전 가드: PULLUP targetReps ≤ currentMaxPullups + 1
            val safePullupReps = targetMaxReps.coerceAtMost(currentMaxPullups + 1)
            if (safePullupReps >= 1) {
                add(TaskExecutionDraft(ExerciseType.PULLUP, 3, safePullupReps, 120))
            }
            add(TaskExecutionDraft(ExerciseType.DEAD_HANG, 1, 30, 60))
        }

        TrainingPhase.PEAK -> {
            val safePullupReps = targetMaxReps
                .coerceAtMost(currentMaxPullups + 1)
                .coerceAtLeast(1)
            listOf(
                TaskExecutionDraft(ExerciseType.PULLUP, 3, safePullupReps, 120),
                TaskExecutionDraft(ExerciseType.NEGATIVE, 2, 5, 120),
                TaskExecutionDraft(ExerciseType.DEAD_HANG, 1, 30, 60),
            )
        }

        TrainingPhase.MAINTAIN -> listOf(
            TaskExecutionDraft(ExerciseType.PULLUP, 3, 10, 120),
        )
    }

    private fun computeTotalVolume(executions: List<TaskExecutionDraft>): Int =
        executions.sumOf {
            (it.targetSets * it.targetReps * it.exerciseType.volumeRatio()).toDouble()
        }.toInt()

    // ----- 일일 분배 (08 §5.2) -----
    // baseDayExecutions = HARD 기준 메뉴. LIGHT/MODERATE는 세트 수만 축소.

    private fun distributeDailyTasks(
        availableDays: Set<DayOfWeek>,
        baseDayExecutions: List<TaskExecutionDraft>,
    ): List<DailyTaskDraft> {
        val sortedDays = availableDays.sortedBy { it.value }
        val pattern = intensityPattern(sortedDays.size)

        return sortedDays.zip(pattern).map { (day, intensity) ->
            if (intensity == Intensity.REST) {
                DailyTaskDraft(day, DayType.REST, Intensity.REST, emptyList())
            } else {
                val dayExecutions = baseDayExecutions
                    .map { exec -> exec.copy(targetSets = scaleSetsByIntensity(exec.targetSets, intensity)) }
                    .filter { it.targetSets > 0 }
                DailyTaskDraft(day, DayType.WORKOUT, intensity, dayExecutions)
            }
        }
    }

    private fun intensityPattern(numDays: Int): List<Intensity> = when (numDays) {
        1 -> listOf(Intensity.HARD)
        2 -> listOf(Intensity.MODERATE, Intensity.HARD)
        3 -> listOf(Intensity.LIGHT, Intensity.MODERATE, Intensity.HARD)
        4 -> listOf(Intensity.LIGHT, Intensity.MODERATE, Intensity.LIGHT, Intensity.HARD)
        5 -> listOf(Intensity.LIGHT, Intensity.MODERATE, Intensity.LIGHT, Intensity.MODERATE, Intensity.HARD)
        6 -> listOf(Intensity.LIGHT, Intensity.MODERATE, Intensity.LIGHT, Intensity.HARD, Intensity.LIGHT, Intensity.MODERATE)
        else -> listOf(
            Intensity.LIGHT, Intensity.MODERATE, Intensity.LIGHT, Intensity.HARD,
            Intensity.LIGHT, Intensity.MODERATE, Intensity.LIGHT,
        )
    }

    private fun scaleSetsByIntensity(baseSets: Int, intensity: Intensity): Int = when (intensity) {
        Intensity.LIGHT -> max(1, (baseSets * 0.5f).roundToInt())
        Intensity.MODERATE -> max(1, (baseSets * 0.75f).roundToInt())
        Intensity.HARD -> baseSets
        Intensity.REST -> 0
    }

    // ----- 마일스톤 (08 §8) -----

    private fun computeMilestones(plans: List<WeeklyPlanDraft>): List<MilestoneDraft> {
        val milestones = mutableListOf<MilestoneDraft>()
        plans.firstOrNull { it.targetMaxReps >= 1 }?.let { milestones += MilestoneDraft("FIRST_PULLUP", it.weekNumber) }
        plans.firstOrNull { it.targetMaxReps >= 3 }?.let { milestones += MilestoneDraft("PULLUP_3", it.weekNumber) }
        plans.firstOrNull { it.targetMaxReps >= 5 }?.let { milestones += MilestoneDraft("PULLUP_5", it.weekNumber) }
        plans.firstOrNull { it.targetMaxReps >= 10 }?.let { milestones += MilestoneDraft("PULLUP_10", it.weekNumber) }
        return milestones
    }

    // ----- 사용자 메시지 -----

    private fun buildNotes(input: SimulationInput, totalWeeks: Int): List<String> {
        val notes = mutableListOf<String>()
        if (input.availableDaysOfWeek.size == 1) {
            notes += "주 1회로는 도달이 어렵습니다. 가능하면 2일 이상을 권장해요."
        }
        if (input.currentMaxPullups >= input.targetReps) {
            notes += "이미 목표를 달성하실 수 있어요! 유지 모드(주 2회 풀업)로 운영해보세요."
        }
        notes += "총 ${totalWeeks}주 계획입니다. 매주 컨디션을 기록하면 다음 주 계획이 자동 조정돼요."
        return notes
    }
}
