package com.mmd.core.simulation

import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 08 문서 §9 단위 테스트. T5/T6 (adjustPlan)은 Phase 5에서 구현 예정이라 생략.
 */
class PullupSimulatorTest {

    private val simulator: PullupSimulator = PullupSimulatorImpl()

    private fun input(
        height: Int = 175,
        weight: Float = 75f,
        age: Int = 30,
        gender: Gender = Gender.MALE,
        currentMax: Int = 1,
        deadHang: Int = 30,
        days: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        target: Int = 10,
        intensity: IntensityPreference = IntensityPreference.NORMAL,
    ) = SimulationInput(
        heightCm = height, weightKg = weight, age = age, gender = gender,
        currentMaxPullups = currentMax, currentDeadHangSeconds = deadHang,
        availableDaysOfWeek = days, targetReps = target, intensityPreference = intensity,
    )

    @Test
    fun `T1 — 기본 케이스 175cm 80kg currentMax=1 deadHang=10 3일 NORMAL`() {
        val result = simulator.generatePlan(input(weight = 80f, currentMax = 1, deadHang = 10))

        assertTrue("totalWeeks ${result.totalWeeks} should be in 9..12", result.totalWeeks in 9..12)

        val foundationCount = result.weeklyPlans.count { it.phase == TrainingPhase.FOUNDATION }
        val buildCount = result.weeklyPlans.count { it.phase == TrainingPhase.BUILD }
        val peakCount = result.weeklyPlans.count { it.phase == TrainingPhase.PEAK }
        assertTrue("FOUNDATION $foundationCount should be 2..3", foundationCount in 2..3)
        assertTrue("BUILD $buildCount should be 5..7", buildCount in 5..7)
        assertTrue("PEAK $peakCount should be 2..3", peakCount in 2..3)

        // Week 1: FOUNDATION → no PULLUP
        val week1 = result.weeklyPlans[0]
        val week1HasPullup = week1.dailyTasks.any { task ->
            task.executions.any { it.exerciseType == ExerciseType.PULLUP }
        }
        assertTrue("week 1 should have no PULLUP for currentMax=1 user", !week1HasPullup)
    }

    @Test
    fun `T2 — 이미 가능한 사용자 currentMax=8 NORMAL 3일`() {
        val result = simulator.generatePlan(input(currentMax = 8))

        assertTrue("totalWeeks ${result.totalWeeks} should be in 6..8", result.totalWeeks in 6..8)

        val week1HasPullup = result.weeklyPlans[0].dailyTasks.any { task ->
            task.executions.any { it.exerciseType == ExerciseType.PULLUP }
        }
        assertTrue("week 1 should have PULLUP for high-capability user", week1HasPullup)

        val peakRatio = result.weeklyPlans.count { it.phase == TrainingPhase.PEAK }.toFloat() / result.totalWeeks
        assertTrue("PEAK ratio $peakRatio should be >= 0.3", peakRatio >= 0.3f)
    }

    @Test
    fun `T3 — 완전 초보 + GENTLE currentMax=0 deadHang=5 2일`() {
        val result = simulator.generatePlan(
            input(
                currentMax = 0, deadHang = 5,
                days = setOf(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY),
                intensity = IntensityPreference.GENTLE,
            ),
        )

        assertEquals("totalWeeks should hit upper bound 16", 16, result.totalWeeks)

        val foundationCount = result.weeklyPlans.count { it.phase == TrainingPhase.FOUNDATION }
        assertTrue("FOUNDATION $foundationCount should be >= 4", foundationCount >= 4)
    }

    @Test
    fun `T4 — 4일주 NORMAL은 T1보다 totalWeeks 감소`() {
        val t1 = simulator.generatePlan(input(weight = 80f, currentMax = 1, deadHang = 10))
        val t4 = simulator.generatePlan(
            input(
                weight = 80f, currentMax = 1, deadHang = 10,
                days = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            ),
        )
        assertTrue("T4 ${t4.totalWeeks} should be < T1 ${t1.totalWeeks}", t4.totalWeeks < t1.totalWeeks)
    }

    @Test
    fun `T7 — 이미 10개 가능 currentMax=12 totalWeeks=6 + week1 PULLUP_10`() {
        val result = simulator.generatePlan(input(currentMax = 12))

        assertEquals(6, result.totalWeeks)

        val pullup10 = result.expectedMilestones.firstOrNull { it.code == "PULLUP_10" }
        assertEquals("PULLUP_10 should be expected on week 1", 1, pullup10?.expectedWeek)
    }

    @Test
    fun `T8 — 1일주는 16주 + 권장 노트`() {
        val result = simulator.generatePlan(
            input(currentMax = 1, days = setOf(DayOfWeek.SUNDAY)),
        )
        assertEquals(16, result.totalWeeks)
        assertTrue(
            "notes should mention 2일 이상 권장",
            result.notes.any { it.contains("2일 이상") },
        )
    }

    @Test
    fun `T9 — deterministic, 같은 입력 같은 출력`() {
        val a = simulator.generatePlan(input(currentMax = 3))
        val b = simulator.generatePlan(input(currentMax = 3))
        assertEquals(a, b)
    }

    @Test
    fun `T10 — 안전 가드, PULLUP targetReps는 currentMaxPullups + 1을 절대 초과하지 않음`() {
        val result = simulator.generatePlan(input(currentMax = 1))
        result.weeklyPlans.forEach { week ->
            week.dailyTasks.forEach { day ->
                day.executions
                    .filter { it.exerciseType == ExerciseType.PULLUP }
                    .forEach { exec ->
                        assertTrue(
                            "week ${week.weekNumber}: PULLUP targetReps ${exec.targetReps} > currentMax+1 (2)",
                            exec.targetReps <= 2,
                        )
                    }
            }
        }
    }
}
