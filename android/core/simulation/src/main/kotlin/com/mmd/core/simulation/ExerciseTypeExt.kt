package com.mmd.core.simulation

/**
 * 운동 종목별 풀업 환산 비율 (08 §3).
 * Volume = sets × reps(or seconds) × ratio.
 * DEAD_HANG의 경우 reps 위치에 초가 들어가므로 동일 공식이 작동.
 */
fun ExerciseType.volumeRatio(): Float = when (this) {
    ExerciseType.PULLUP -> 1.0f
    ExerciseType.ASSISTED_PULLUP -> 0.4f
    ExerciseType.NEGATIVE -> 0.6f
    ExerciseType.AUSTRALIAN_PULLUP -> 0.3f
    ExerciseType.DEAD_HANG -> 0.05f
}
