package com.mmd.core.domain.mapper

import com.mmd.core.simulation.DailyTaskDraft
import com.mmd.core.simulation.DayType
import com.mmd.core.simulation.ExerciseType
import com.mmd.core.simulation.Intensity
import com.mmd.core.simulation.MilestoneDraft
import com.mmd.core.simulation.SimulationResult
import com.mmd.core.simulation.TaskExecutionDraft
import com.mmd.core.simulation.TrainingPhase
import com.mmd.core.simulation.WeeklyPlanDraft
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationResultMapperTest {

    private val planStart = LocalDate.of(2026, 5, 4)   // Monday

    @Test
    fun `week 1 startDate equals planStartDate, endDate is 6 days later`() {
        val result = simpleResult(weeks = 2)
        val plans = result.toDomainPlans(planStart)

        assertEquals(LocalDate.of(2026, 5, 4), plans[0].startDate)
        assertEquals(LocalDate.of(2026, 5, 10), plans[0].endDate)
    }

    @Test
    fun `week N startDate is planStartDate + (N-1) weeks`() {
        val result = simpleResult(weeks = 3)
        val plans = result.toDomainPlans(planStart)

        assertEquals(LocalDate.of(2026, 5, 4), plans[0].startDate)
        assertEquals(LocalDate.of(2026, 5, 11), plans[1].startDate)
        assertEquals(LocalDate.of(2026, 5, 18), plans[2].startDate)
    }

    @Test
    fun `dailyTask date maps from dayOfWeek with Monday as offset 0`() {
        val result = SimulationResult(
            totalWeeks = 1,
            weeklyPlans = listOf(
                WeeklyPlanDraft(
                    weekNumber = 1,
                    phase = TrainingPhase.FOUNDATION,
                    targetMaxReps = 0,
                    totalVolume = 0,
                    dailyTasks = listOf(
                        workoutTask(DayOfWeek.MONDAY),
                        workoutTask(DayOfWeek.WEDNESDAY),
                        workoutTask(DayOfWeek.FRIDAY),
                    ),
                ),
            ),
            expectedMilestones = emptyList(),
            notes = emptyList(),
        )

        val plans = result.toDomainPlans(planStart)
        val dates = plans[0].dailyTasks.map { it.date }
        assertEquals(
            listOf(LocalDate.of(2026, 5, 4), LocalDate.of(2026, 5, 6), LocalDate.of(2026, 5, 8)),
            dates,
        )
    }

    @Test
    fun `summary text generates from executions`() {
        val result = SimulationResult(
            totalWeeks = 1,
            weeklyPlans = listOf(
                WeeklyPlanDraft(
                    weekNumber = 1,
                    phase = TrainingPhase.FOUNDATION,
                    targetMaxReps = 0,
                    totalVolume = 0,
                    dailyTasks = listOf(
                        DailyTaskDraft(
                            dayOfWeek = DayOfWeek.MONDAY,
                            dayType = DayType.WORKOUT,
                            intensity = Intensity.LIGHT,
                            executions = listOf(
                                TaskExecutionDraft(ExerciseType.ASSISTED_PULLUP, 3, 5, 90),
                                TaskExecutionDraft(ExerciseType.DEAD_HANG, 2, 30, 60),
                            ),
                        ),
                    ),
                ),
            ),
            expectedMilestones = emptyList(),
            notes = emptyList(),
        )

        val task = result.toDomainPlans(planStart)[0].dailyTasks[0]
        assertEquals("어시스트 풀업 5회×3, 매달리기 30초×2", task.summary)
    }

    @Test
    fun `REST day summary is 휴식 with empty executions`() {
        val result = SimulationResult(
            totalWeeks = 1,
            weeklyPlans = listOf(
                WeeklyPlanDraft(
                    weekNumber = 1,
                    phase = TrainingPhase.FOUNDATION,
                    targetMaxReps = 0,
                    totalVolume = 0,
                    dailyTasks = listOf(
                        DailyTaskDraft(
                            dayOfWeek = DayOfWeek.SUNDAY,
                            dayType = DayType.REST,
                            intensity = Intensity.REST,
                            executions = emptyList(),
                        ),
                    ),
                ),
            ),
            expectedMilestones = emptyList(),
            notes = emptyList(),
        )

        val task = result.toDomainPlans(planStart)[0].dailyTasks[0]
        assertEquals("휴식", task.summary)
        assertTrue(task.executions.isEmpty())
    }

    @Test
    fun `WeeklyPlan preserves weekNumber, phase, targetMaxReps, totalVolume`() {
        val result = SimulationResult(
            totalWeeks = 1,
            weeklyPlans = listOf(
                WeeklyPlanDraft(
                    weekNumber = 7,
                    phase = TrainingPhase.PEAK,
                    targetMaxReps = 9,
                    totalVolume = 42,
                    dailyTasks = emptyList(),
                ),
            ),
            expectedMilestones = emptyList(),
            notes = emptyList(),
        )

        val plan = result.toDomainPlans(planStart)[0]
        assertEquals(7, plan.weekNumber)
        assertEquals(TrainingPhase.PEAK, plan.phase)
        assertEquals(9, plan.targetMaxReps)
        assertEquals(42, plan.totalVolume)
        assertEquals(0L, plan.id)             // auto-gen by Room later
    }

    @Test
    fun `TaskExecution maps targetSets, targetReps, restSeconds`() {
        val result = SimulationResult(
            totalWeeks = 1,
            weeklyPlans = listOf(
                WeeklyPlanDraft(
                    weekNumber = 1,
                    phase = TrainingPhase.BUILD,
                    targetMaxReps = 1,
                    totalVolume = 30,
                    dailyTasks = listOf(
                        DailyTaskDraft(
                            dayOfWeek = DayOfWeek.MONDAY,
                            dayType = DayType.WORKOUT,
                            intensity = Intensity.HARD,
                            executions = listOf(
                                TaskExecutionDraft(ExerciseType.PULLUP, 3, 5, 120),
                            ),
                        ),
                    ),
                ),
            ),
            expectedMilestones = listOf(MilestoneDraft("FIRST_PULLUP", 1)),
            notes = emptyList(),
        )

        val exec = result.toDomainPlans(planStart)[0].dailyTasks[0].executions[0]
        assertEquals(ExerciseType.PULLUP, exec.exerciseType)
        assertEquals(3, exec.targetSets)
        assertEquals(5, exec.targetReps)
        assertEquals(120, exec.restSeconds)
        assertTrue(exec.records.isEmpty())
        assertEquals(0L, exec.id)
    }

    // -- helpers --

    private fun simpleResult(weeks: Int): SimulationResult = SimulationResult(
        totalWeeks = weeks,
        weeklyPlans = (1..weeks).map { w ->
            WeeklyPlanDraft(
                weekNumber = w,
                phase = TrainingPhase.FOUNDATION,
                targetMaxReps = 0,
                totalVolume = 0,
                dailyTasks = emptyList(),
            )
        },
        expectedMilestones = emptyList(),
        notes = emptyList(),
    )

    private fun workoutTask(day: DayOfWeek): DailyTaskDraft = DailyTaskDraft(
        dayOfWeek = day,
        dayType = DayType.WORKOUT,
        intensity = Intensity.MODERATE,
        executions = listOf(TaskExecutionDraft(ExerciseType.DEAD_HANG, 1, 30, 60)),
    )
}
