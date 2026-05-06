package com.mmd.core.simulation

interface PullupSimulator {
    /** 사용자 입력으로 12주 계획을 생성. 구현은 Phase 2. */
    fun generatePlan(input: SimulationInput): SimulationResult

    // adjustPlan(...): Phase 5(동적 재조정)에서 추가
}
