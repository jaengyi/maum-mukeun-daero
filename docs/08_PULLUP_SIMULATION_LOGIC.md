# 08. 턱걸이 시뮬레이션 알고리즘 명세

> Module: `core-simulation` (순수 Kotlin, 안드로이드 의존성 없음)
> 본 문서는 PullupSimulator 의 구현 명세이며, 단위 테스트의 truth source 입니다.

> **면책**: 본 문서의 알고리즘은 일반적인 트레이닝 가이드를 단순화한 모델입니다. 실제 운동 생리학과 개인차를 완전히 반영하지 않으며, MVP 검증 후 개선될 예정입니다. **사용자에게도 같은 면책을 명시합니다.**

---

## 1. 입력 / 출력 정의

### 1.1 SimulationInput

```kotlin
data class SimulationInput(
    val heightCm: Int,                       // 100~250
    val weightKg: Float,                     // 30~200
    val age: Int,                            // 10~80 (계산: 현재 연도 - birthYear)
    val gender: Gender,                      // MALE / FEMALE / OTHER
    val currentMaxPullups: Int,              // 0~30 (현재 한 번에 가능한 풀업 횟수)
    val currentDeadHangSeconds: Int,         // 0~120 (매달리기 가능 시간)
    val availableDaysOfWeek: Set<DayOfWeek>, // 운동 가능 요일
    val targetReps: Int = 10,                // 목표 횟수 (MVP는 10 고정)
    val intensityPreference: IntensityPreference = IntensityPreference.NORMAL
)

enum class IntensityPreference { GENTLE, NORMAL, AGGRESSIVE }
```

### 1.2 SimulationResult

```kotlin
data class SimulationResult(
    val totalWeeks: Int,                     // 예상 총 주차
    val weeklyPlans: List<WeeklyPlanDraft>,
    val expectedMilestones: List<Milestone>, // 주요 마일스톤
    val notes: List<String>                  // 사용자 안내 메시지
)

data class WeeklyPlanDraft(
    val weekNumber: Int,
    val phase: TrainingPhase,                // FOUNDATION / BUILD / PEAK / MAINTAIN
    val targetMaxReps: Int,                  // 이 주에 도달할 풀업 1회 가능 추정치
    val totalVolume: Int,                    // 주간 총 환산 횟수
    val dailyTasks: List<DailyTaskDraft>
)

data class DailyTaskDraft(
    val dayOfWeek: DayOfWeek,
    val dayType: DayType,                    // WORKOUT / REST
    val intensity: Intensity,
    val executions: List<TaskExecutionDraft>
)

data class TaskExecutionDraft(
    val exerciseType: ExerciseType,          // PULLUP / ASSISTED_PULLUP / NEGATIVE / DEAD_HANG / AUSTRALIAN_PULLUP
    val targetSets: Int,
    val targetReps: Int,                     // 시간 측정 종목은 초 단위
    val restSeconds: Int
)
```

## 2. 핵심 개념

### 2.1 능력 계수 (Strength Score)

사용자의 현재 풀업 능력을 정량화한 0~100 점수.

```
strengthScore = baseFromMaxReps + deadHangBonus - bodyLoadPenalty
```

| 항목 | 계산 |
|---|---|
| `baseFromMaxReps` | 0개=0, 1개=15, 2개=25, 3개=35, 5개=50, 7개=65, 10개=80, 15개=95 (선형 보간) |
| `deadHangBonus` | `min(15, currentDeadHangSeconds / 4)` (60초 매달리기 = +15) |
| `bodyLoadPenalty` | BMI > 27 일 때: `(BMI - 27) × 2` (최대 10점 감점) |

**예시**:
- 김작심 (175cm, 80kg, BMI 26.1, 풀업 1개, 매달리기 10초)
  - baseFromMaxReps = 15
  - deadHangBonus = 2 (10/4 = 2.5 → 2)
  - bodyLoadPenalty = 0
  - **strengthScore = 17**

### 2.2 목표 능력 점수

턱걸이 10개 = strengthScore 80점.

### 2.3 점수 격차 → 주차 수 환산

```
gap = 80 - currentScore
totalWeeks = clamp(ceil(gap / weeklyGain), 6, 16)
```

- `weeklyGain` 기본값:
  - GENTLE = 4점/주
  - NORMAL = 6점/주
  - AGGRESSIVE = 8점/주
- `clamp(_, 6, 16)`: 너무 빠르거나(<6주) 너무 길지(>16주) 않도록 안전 범위
- 운동 가능 요일이 적을수록 weeklyGain이 줄어듦 (§2.4 참고)

### 2.4 주간 빈도 보정

| 주 운동일 | weeklyGain 배수 |
|---|---|
| 1일 | 0.4 |
| 2일 | 0.7 |
| 3일 | 1.0 (기준) |
| 4일 | 1.15 |
| 5일+ | 1.2 (오버트레이닝 우려로 1.2 상한) |

**보정된 weeklyGain** = `baseWeeklyGain × frequencyMultiplier`

### 2.5 페이즈 (TrainingPhase)

| Phase | 비율 | 핵심 |
|---|---|---|
| FOUNDATION | 첫 25% 주차 | 매달리기, 어시스트, 호주 풀업 — 등·코어 활성화 |
| BUILD | 다음 50% | 네거티브 풀업 + 첫 풀업 도전 + 점진적 횟수 증가 |
| PEAK | 마지막 25% | 풀업 위주, 세트당 횟수 폭발적 증가 |
| MAINTAIN | 목표 달성 후 | 주 2회 유지 모드 |

**예시**: 12주 계획이라면 FOUNDATION 1~3주, BUILD 4~9주, PEAK 10~12주.

## 3. 운동 종목 정의

| 종목 | 설명 | 풀업 환산 비율 |
|---|---|---|
| **PULLUP** | 정통 풀업 (오버그립, 가슴까지) | 1.0 |
| **ASSISTED_PULLUP** | 밴드 / 박스 어시스트 풀업 | 0.4 |
| **NEGATIVE** | 점프해 올라간 뒤 천천히 내려오기 (3~5초) | 0.6 |
| **AUSTRALIAN_PULLUP** | 호주 풀업 / 인버티드 로우 (수평 당기기) | 0.3 |
| **DEAD_HANG** | 매달리기 (초 단위) | 초당 0.05 (20초 = 1회 환산) |

> 환산 비율은 "1회 정통 풀업이 만드는 자극을 기준"으로 한 근사치.

## 4. 주차별 종목 구성 규칙

### 4.1 FOUNDATION
- 매달리기: 30초 × 3세트 (또는 본인 한계의 80%)
- 호주 풀업: 8~10회 × 3세트
- 어시스트 풀업: 5회 × 2~3세트
- 네거티브 0~2회 × 2세트 (FOUNDATION 후반)

### 4.2 BUILD
- 네거티브 풀업: 3~5회 × 3세트 (메인)
- 어시스트 풀업: 5~8회 × 2세트
- 풀업: `targetMaxReps` 회 × 3세트 (가능해진 시점부터)
- 매달리기: 30초 × 1세트 (마무리)

### 4.3 PEAK
- 풀업: 점진적 증가, 세트당 목표가 5→7→8→9→10
- 네거티브: 보조로 1~2세트
- 매달리기: 회복 운동으로 1세트

### 4.4 MAINTAIN
- 풀업: `targetReps` × 3세트 주 2회 (유지)

## 5. 세트 / 횟수 산출 공식

### 5.1 주간 능력 추정 (`weeklyTargetMaxReps`)

```
weeklyTargetMaxReps[w] = round(maxRepsFromScore(currentScore + weeklyGain × w))
```

`maxRepsFromScore` (역함수):
- 0~15점 → 0개
- 15~25점 → 1개
- 25~35점 → 2개
- 35~50점 → 3개
- 50~65점 → 5개
- 65~80점 → 7~9개
- 80점+ → 10개

> 정확한 보간이 아닌 **단계함수 + 약간의 보간**으로 처리. 너무 정밀하게 만들 필요 없음 — 사용자가 매주 본인 능력을 기록하면 §7의 적응형 조정이 보정한다.

### 5.2 일일 운동 강도

| Intensity | 기준 |
|---|---|
| LIGHT | 그 주 totalVolume × 25% |
| MODERATE | 그 주 totalVolume × 35% |
| HARD | 그 주 totalVolume × 40% |
| REST | 0 |

운동일이 3일이면 LIGHT-MODERATE-HARD 또는 MODERATE-LIGHT-HARD 등으로 분배. 연속 HARD 금지 (반드시 LIGHT 또는 REST 사이에 끼움).

### 5.3 휴식 시간 기본값
- 풀업 / 네거티브: 90~120초
- 어시스트 / 호주 풀업: 60~90초
- 매달리기 사이: 60초

## 6. 알고리즘 의사 코드

```kotlin
fun generatePlan(input: SimulationInput): SimulationResult {
    // 1. 현재 점수 계산
    val currentScore = computeStrengthScore(input)

    // 2. 주간 게인 산출
    val baseGain = when (input.intensityPreference) {
        GENTLE -> 4f; NORMAL -> 6f; AGGRESSIVE -> 8f
    }
    val freqMul = frequencyMultiplier(input.availableDaysOfWeek.size)
    val weeklyGain = baseGain * freqMul

    // 3. 총 주차
    val gap = (80f - currentScore).coerceAtLeast(8f) // 안전 하한
    val totalWeeks = ((gap / weeklyGain).toInt() + 1).coerceIn(6, 16)

    // 4. 페이즈 경계
    val foundationEnd = (totalWeeks * 0.25).toInt().coerceAtLeast(2)
    val buildEnd = (totalWeeks * 0.75).toInt().coerceAtLeast(foundationEnd + 2)

    // 5. 주차별 plan 생성
    val plans = (1..totalWeeks).map { w ->
        val phase = when {
            w <= foundationEnd -> TrainingPhase.FOUNDATION
            w <= buildEnd      -> TrainingPhase.BUILD
            else               -> TrainingPhase.PEAK
        }
        val targetMaxReps = maxRepsFromScore(currentScore + weeklyGain * w)
        val executions = composeWeekExecutions(phase, targetMaxReps, currentScore)
        val totalVolume = executions.sumOf { it.targetSets * it.targetReps * exerciseRatio(it.exerciseType) }.toInt()

        // 일일 분배
        val dailyTasks = distributeDailyTasks(
            availableDays = input.availableDaysOfWeek,
            phase = phase,
            executions = executions
        )

        WeeklyPlanDraft(
            weekNumber = w,
            phase = phase,
            targetMaxReps = targetMaxReps,
            totalVolume = totalVolume,
            dailyTasks = dailyTasks
        )
    }

    // 6. 마일스톤 산출
    val milestones = computeMilestones(plans)

    // 7. 사용자 메시지
    val notes = buildNotes(input, plans, totalWeeks)

    return SimulationResult(totalWeeks, plans, milestones, notes)
}
```

## 7. 적응형 조정 (`adjustPlan`)

매주 일요일(또는 주 마지막 운동일) 다음 주의 계획을 동적으로 조정한다.

### 7.1 입력

```kotlin
data class ProgressLog(
    val weekNumber: Int,
    val planned: Int,         // 예정된 운동일 수
    val completed: Int,       // 실제 완료 수
    val totalRepsExpected: Int,
    val totalRepsActual: Int,
    val newPullupRecord: Int? // 그 주 최대 1회 풀업 기록 (있으면)
)

data class ConditionLog(
    val avgConditionScore: Float, // 1.0~5.0
    val skippedDueToFatigue: Boolean
)
```

### 7.2 분기 로직

```
completionRate = completed / planned
volumeRate     = totalRepsActual / totalRepsExpected

상태 분기:
- BURNOUT     : avgCondition <= 2.5  AND  completionRate < 0.5
- UNDER_LOAD  : completionRate < 0.7  (BURNOUT 아닐 때)
- ON_TRACK    : 0.7 <= completionRate <= 1.1  AND  avgCondition >= 3.0
- OVER_PERFORM: completionRate > 1.0  AND  volumeRate > 1.15  AND  avgCondition >= 4.0
```

### 7.3 조정 액션

| 상태 | 다음 주 조정 |
|---|---|
| **BURNOUT** | 강도 -1단계, 운동일 1일 줄이기 (-1), "회복 주차" 표시, 메시지: "잠깐 숨 고르고 가요" |
| **UNDER_LOAD** | 강도 유지, 세트 수 -1, 메시지: "오늘은 가볍게라도 시작해봐요" |
| **ON_TRACK** | 원래 계획 유지, 메시지: "순항 중이에요" |
| **OVER_PERFORM** | 강도 +1단계, 세트당 횟수 +1, 메시지: "잘하고 있어요. 살짝 더 도전해볼까요?" |

### 7.4 안전 가드
- 강도는 한 번에 1단계만 변동 (급격 조정 금지)
- 정통 풀업은 사용자의 `currentMaxPullups + 1`을 절대 넘지 않게 세트 횟수 책정
- 신규 풀업 기록 갱신 시 (newPullupRecord ≥ 이전 max + 2) 다음 주에 풀업 비중을 자동 증가

## 8. 마일스톤 정의

| code | 조건 |
|---|---|
| `STREAK_3` | 3일 연속 미션 완료 |
| `STREAK_7` | 7일 연속 미션 완료 (휴식일도 카운트) |
| `STREAK_30` | 30일 연속 |
| `NEGATIVE_5` | 네거티브 풀업 5회 1세트 달성 |
| `FIRST_PULLUP` | 첫 정통 풀업 1회 |
| `PULLUP_3` | 풀업 3회 1세트 |
| `PULLUP_5` | 풀업 5회 1세트 |
| `PULLUP_10` | 풀업 10회 1세트 (목표 달성) |
| `DEAD_HANG_60` | 매달리기 60초 |

## 9. 단위 테스트 케이스 (필수)

`core-simulation/src/test/...PullupSimulatorTest.kt` 에서 다음 케이스 모두 통과해야 함.

### T1. 기본 케이스
- Input: 175cm, 80kg, 32세, MALE, currentMax=1, deadHang=10, 운동 3일/주, NORMAL
- Expected:
  - totalWeeks ∈ [9, 12]
  - phase 분포: FOUNDATION 2~3주, BUILD 5~7주, PEAK 2~3주
  - week 1은 PULLUP 종목이 없거나 매우 적게 포함

### T2. 이미 가능한 사용자
- Input: currentMax=8, NORMAL, 3일/주
- Expected:
  - totalWeeks ∈ [6, 8]
  - week 1부터 PULLUP 메인
  - PEAK 비중 큼

### T3. 완전 초보 + GENTLE
- Input: currentMax=0, deadHang=5, GENTLE, 2일/주
- Expected:
  - totalWeeks = 16 (상한)
  - FOUNDATION 비중 ≥ 4주
  - 매달리기·호주 풀업·어시스트 위주

### T4. 4일/주 NORMAL
- T1과 동일 단, 4일/주
- Expected:
  - T1보다 totalWeeks 감소
  - 일일 강도 분포에 LIGHT 1일 추가

### T5. 적응형 조정 - BURNOUT
- 12주 계획 중 5주차에 완료율 30%, 컨디션 평균 2.0
- adjustPlan 호출 후
- Expected:
  - 6주차 weeklyTargetMaxReps 가 5주차 대비 같거나 ↓
  - 6주차 totalVolume 감소 (>= 20%)
  - 6주차 운동일 1일 감소

### T6. 적응형 조정 - OVER_PERFORM
- 5주차에 완료율 100%, 실제 볼륨 +20%, 컨디션 평균 4.5
- adjustPlan 호출 후
- Expected:
  - 6주차 totalVolume +10~15%
  - 6주차에 PULLUP 종목 비중 증가

### T7. 엣지 - 이미 10개 가능
- currentMax = 12
- Expected:
  - totalWeeks 최소값 (6주)
  - 첫 주에 PULLUP_10 마일스톤 즉시 달성 표기
  - MAINTAIN 페이즈 권장 메시지

### T8. 엣지 - 1일/주
- 운동 가능 요일 1일
- Expected:
  - totalWeeks = 16 (상한)
  - "주 1회로는 도달이 어렵습니다. 가능하면 2일 이상을 권장해요" 노트 포함

### T9. 결정성
- 같은 입력으로 두 번 호출 → 결과 동일 (시뮬레이터는 deterministic)

### T10. 안전 가드
- currentMax=1 인 사용자에게 1주차에 PULLUP 4세트 같은 무리한 메뉴가 절대 나오지 않음
- 어떤 주차의 PULLUP targetReps도 (예측 가능 횟수 + 1) 초과하지 않음

## 10. 시뮬레이션 결과 시각화 (참고)

S6 화면의 라인 차트는 `weeklyTargetMaxReps` 시퀀스를 그대로 그린다:

```
주차:    1   2   3   4   5   6   7   8   9   10
target:  0   1   2   3   3   5   6   7   8   10
```

이 곡선은 사용자에게 "내가 언제쯤 첫 풀업이 가능할지"를 시각적으로 약속하는 역할을 한다.

## 11. 추후 개선 (Phase 1+)

- **개인 데이터 학습**: 본인 베타 4주 데이터로 가중치 보정
- **bodyweight 변화 반영**: 사용자가 체중을 줄이면 strengthScore 자동 상향
- **부상 / 통증 입력**: 통증 기록 시 다음 주 강도 자동 -1
- **연령대 보정**: 40+, 50+ 사용자에게 회복일 더 많이
- **머신러닝 모델 (장기)**: 다수 사용자 데이터 누적 시 예측 모델 도입 검토 (단, 개인정보 보호 우선)

## 12. 구현 시 주의사항

1. **순수 Kotlin**: `core-simulation`은 Android API 의존성 0
2. **deterministic**: 같은 입력은 항상 같은 출력 (Random 사용 시 seed 명시)
3. **방어적 입력 검증**: 입력 범위를 벗어나면 즉시 `IllegalArgumentException`
4. **단위 테스트 우선 (TDD 권장)**: 명세 → 테스트 → 구현 순서
5. **알고리즘 변경 시**: 본 문서를 먼저 업데이트하고 commit, 그 후 코드 변경
