package com.mmd.core.domain.usecase

import com.mmd.core.simulation.PullupSimulator
import com.mmd.core.simulation.SimulationInput
import com.mmd.core.simulation.SimulationResult
import javax.inject.Inject

/**
 * 시뮬레이션 결과 생성. ViewModel이 S6 미리보기를 위해 호출.
 * 저장은 [CompleteOnboardingUseCase]가 담당.
 */
class GeneratePlanUseCase @Inject constructor(
    private val simulator: PullupSimulator,
) {
    operator fun invoke(input: SimulationInput): SimulationResult = simulator.generatePlan(input)
}
