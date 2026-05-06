# 04. 데이터 모델 / DB 스키마 (Data Model)

> Storage: Room (SQLite)
> DB Name: `mmd.db`
> Schema Version: 1

---

## 1. ER 개념도

```
┌──────────────┐
│ UserProfile  │  (1 row, singleton)
└──────┬───────┘
       │
       │ 1
       ▼
┌──────────────┐  1     N  ┌──────────────┐  1   N  ┌──────────────┐
│     Goal     │ ────────▶│  WeeklyPlan  │────────▶│   DailyTask  │
└──────────────┘          └──────────────┘         └──────┬───────┘
                                                          │ 1
                                                          ▼
                                                   ┌──────────────┐  1  N  ┌─────────────┐
                                                   │ TaskExecution│───────▶│  SetRecord  │
                                                   └──────────────┘        └─────────────┘

┌──────────────┐
│ DailyCondition│ (날짜별 컨디션)
└──────────────┘

┌──────────────┐
│  GrassCell   │ (잔디 그래프 저장 — 계산된 값 캐시)
└──────────────┘

┌──────────────┐
│  Milestone   │ (배지 / 마일스톤 달성 기록)
└──────────────┘
```

## 2. 엔티티별 명세

### 2.1 UserProfile

사용자 프로파일 (싱글톤 — 단일 사용자 가정).

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | INTEGER | PK, default 1 | 항상 1 |
| `nickname` | TEXT | NOT NULL | 닉네임 |
| `gender` | TEXT | NOT NULL | "MALE" / "FEMALE" / "OTHER" |
| `birth_year` | INTEGER | NOT NULL | 4자리 |
| `height_cm` | REAL | NOT NULL | 키 (cm) |
| `weight_kg` | REAL | NOT NULL | 몸무게 (kg) |
| `created_at` | INTEGER | NOT NULL | epoch millis |
| `updated_at` | INTEGER | NOT NULL | epoch millis |

```kotlin
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val nickname: String,
    val gender: String,
    val birthYear: Int,
    val heightCm: Float,
    val weightKg: Float,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 2.2 Goal

사용자가 설정한 목표 (현재는 단일 활성 목표; 데이터 모델은 다중 지원으로 설계).

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | INTEGER | PK AUTO | |
| `category` | TEXT | NOT NULL | "PULLUP" (MVP는 단일 값) |
| `target_value` | INTEGER | NOT NULL | 10 (턱걸이 10개) |
| `target_unit` | TEXT | NOT NULL | "REPS" |
| `initial_max_reps` | INTEGER | NOT NULL | 시작 시점 가능 횟수 |
| `initial_dead_hang_sec` | INTEGER | NOT NULL | 시작 시점 매달리기 초 |
| `available_days` | TEXT | NOT NULL | "MON,WED,FRI" 형식 |
| `is_active` | INTEGER | NOT NULL | 0/1 |
| `started_at` | INTEGER | NOT NULL | epoch millis |
| `completed_at` | INTEGER | NULL | 달성 시 |
| `archived_at` | INTEGER | NULL | 보관 시 |

```kotlin
@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val targetValue: Int,
    val targetUnit: String,
    val initialMaxReps: Int,
    val initialDeadHangSec: Int,
    val availableDays: String,
    val isActive: Boolean,
    val startedAt: Long,
    val completedAt: Long?,
    val archivedAt: Long?
)
```

### 2.3 WeeklyPlan

시뮬레이션이 만든 주차별 계획.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | INTEGER | PK AUTO | |
| `goal_id` | INTEGER | FK → goal.id | |
| `week_number` | INTEGER | NOT NULL | 1, 2, 3, ... |
| `start_date` | TEXT | NOT NULL | ISO 8601 (YYYY-MM-DD) |
| `end_date` | TEXT | NOT NULL | YYYY-MM-DD |
| `phase` | TEXT | NOT NULL | "FOUNDATION" / "BUILD" / "PEAK" / "MAINTAIN" |
| `target_max_reps` | INTEGER | NOT NULL | 이 주의 목표 풀업 최대치 |
| `total_volume` | INTEGER | NOT NULL | 주간 총 횟수 (모든 변형 포함) |
| `is_adjusted` | INTEGER | NOT NULL | 동적 재조정 여부 |
| `notes` | TEXT | NULL | 사용자 또는 시스템 메모 |

```kotlin
@Entity(
    tableName = "weekly_plan",
    foreignKeys = [
        ForeignKey(entity = GoalEntity::class,
                   parentColumns = ["id"],
                   childColumns = ["goalId"],
                   onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("goalId"), Index("startDate")]
)
data class WeeklyPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val weekNumber: Int,
    val startDate: String,
    val endDate: String,
    val phase: String,
    val targetMaxReps: Int,
    val totalVolume: Int,
    val isAdjusted: Boolean,
    val notes: String?
)
```

### 2.4 DailyTask

일일 미션 (운동일 / 휴식일 모두 포함).

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | INTEGER | PK AUTO | |
| `weekly_plan_id` | INTEGER | FK | |
| `date` | TEXT | NOT NULL, UNIQUE | YYYY-MM-DD |
| `day_type` | TEXT | NOT NULL | "WORKOUT" / "REST" |
| `intensity_level` | TEXT | NOT NULL | "LIGHT" / "MODERATE" / "HARD" / "REST" |
| `summary` | TEXT | NOT NULL | "어시스트 풀업 5×3, 네거티브 3×3" 등 요약 |
| `is_completed` | INTEGER | NOT NULL | 0/1 |
| `completed_at` | INTEGER | NULL | 완료 시각 epoch millis |

```kotlin
@Entity(
    tableName = "daily_task",
    foreignKeys = [
        ForeignKey(entity = WeeklyPlanEntity::class,
                   parentColumns = ["id"],
                   childColumns = ["weeklyPlanId"],
                   onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("weeklyPlanId"), Index(value = ["date"], unique = true)]
)
data class DailyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weeklyPlanId: Long,
    val date: String,
    val dayType: String,
    val intensityLevel: String,
    val summary: String,
    val isCompleted: Boolean,
    val completedAt: Long?
)
```

### 2.5 TaskExecution

DailyTask의 세부 실행 단위 (세트 그룹).

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | INTEGER PK | |
| `daily_task_id` | INTEGER FK | |
| `exercise_type` | TEXT | "PULLUP" / "ASSISTED_PULLUP" / "NEGATIVE" / "DEAD_HANG" / "AUSTRALIAN_PULLUP" |
| `target_sets` | INTEGER | 목표 세트 수 |
| `target_reps` | INTEGER | 세트당 목표 횟수 (DEAD_HANG의 경우 초) |
| `rest_seconds` | INTEGER | 세트 간 휴식 |
| `order_in_task` | INTEGER | Task 안에서의 순서 |

```kotlin
@Entity(
    tableName = "task_execution",
    foreignKeys = [
        ForeignKey(entity = DailyTaskEntity::class,
                   parentColumns = ["id"],
                   childColumns = ["dailyTaskId"],
                   onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("dailyTaskId")]
)
data class TaskExecutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyTaskId: Long,
    val exerciseType: String,
    val targetSets: Int,
    val targetReps: Int,
    val restSeconds: Int,
    val orderInTask: Int
)
```

### 2.6 SetRecord

실제 수행한 세트 기록.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | INTEGER PK | |
| `task_execution_id` | INTEGER FK | |
| `set_number` | INTEGER | 1, 2, 3... |
| `actual_reps` | INTEGER | 실제 횟수 |
| `actual_seconds` | INTEGER NULL | 매달리기 등 시간 측정 종목 |
| `recorded_at` | INTEGER | epoch millis |

```kotlin
@Entity(
    tableName = "set_record",
    foreignKeys = [
        ForeignKey(entity = TaskExecutionEntity::class,
                   parentColumns = ["id"],
                   childColumns = ["taskExecutionId"],
                   onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("taskExecutionId"), Index("recordedAt")]
)
data class SetRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskExecutionId: Long,
    val setNumber: Int,
    val actualReps: Int,
    val actualSeconds: Int?,
    val recordedAt: Long
)
```

### 2.7 DailyCondition

날짜별 컨디션 / 메모.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `date` | TEXT PK | YYYY-MM-DD (PK) |
| `condition_score` | INTEGER | 1~5 |
| `weight_kg` | REAL NULL | 그날 측정 시 |
| `note` | TEXT NULL | 자유 메모 |
| `recorded_at` | INTEGER | epoch millis |

```kotlin
@Entity(tableName = "daily_condition")
data class DailyConditionEntity(
    @PrimaryKey val date: String,
    val conditionScore: Int,
    val weightKg: Float?,
    val note: String?,
    val recordedAt: Long
)
```

### 2.8 GrassCell

잔디 그래프용 셀 (계산값 캐시).

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `date` | TEXT PK | YYYY-MM-DD |
| `intensity_level` | INTEGER | 0~4 (잔디 색 단계) |
| `total_reps` | INTEGER | 그날 누적 풀업 환산 횟수 |
| `is_workout_day` | INTEGER | 0/1 |
| `is_completed` | INTEGER | 0/1 |
| `updated_at` | INTEGER | epoch millis |

```kotlin
@Entity(tableName = "grass_cell")
data class GrassCellEntity(
    @PrimaryKey val date: String,
    val intensityLevel: Int,
    val totalReps: Int,
    val isWorkoutDay: Boolean,
    val isCompleted: Boolean,
    val updatedAt: Long
)
```

> **설계 참고**: GrassCell은 SetRecord에서 파생되는 값이지만, 잔디 화면에서 12주분(84일)을 빠르게 그리기 위해 미리 계산해 둔다. 세트 기록 시 `recalcDayCell(date)` 트랜잭션으로 갱신한다.

### 2.9 Milestone

마일스톤 / 배지.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | INTEGER PK AUTO | |
| `code` | TEXT UNIQUE | "FIRST_PULLUP", "PULLUP_5", "PULLUP_10", "STREAK_7", "STREAK_30" 등 |
| `title` | TEXT | 표시 이름 |
| `description` | TEXT | 설명 |
| `achieved_at` | INTEGER NULL | 달성 시각, NULL이면 미달성 |

```kotlin
@Entity(
    tableName = "milestone",
    indices = [Index(value = ["code"], unique = true)]
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val achievedAt: Long?
)
```

## 3. DAO 인터페이스 (요약)

```kotlin
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observe(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal WHERE isActive = 1 LIMIT 1")
    fun observeActive(): Flow<GoalEntity?>

    @Insert
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)
}

@Dao
interface PlanDao {
    @Insert
    suspend fun insertWeeklyPlans(plans: List<WeeklyPlanEntity>): List<Long>

    @Insert
    suspend fun insertDailyTasks(tasks: List<DailyTaskEntity>): List<Long>

    @Insert
    suspend fun insertTaskExecutions(execs: List<TaskExecutionEntity>): List<Long>

    @Query("SELECT * FROM daily_task WHERE date = :date LIMIT 1")
    fun observeTodayTask(date: String): Flow<DailyTaskEntity?>

    @Query("""
        SELECT * FROM weekly_plan
        WHERE goalId = :goalId
        ORDER BY weekNumber ASC
    """)
    fun observeWeeklyPlans(goalId: Long): Flow<List<WeeklyPlanEntity>>

    @Transaction
    suspend fun replacePlanFromWeek(goalId: Long, fromWeek: Int, newPlans: List<WeeklyPlanEntity>) {
        // 동적 재조정 시 사용
    }
}

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertSetRecord(record: SetRecordEntity): Long

    @Query("UPDATE daily_task SET isCompleted = :done, completedAt = :ts WHERE id = :id")
    suspend fun markTaskCompleted(id: Long, done: Boolean, ts: Long)

    @Query("""
        SELECT SUM(actualReps) FROM set_record sr
        JOIN task_execution te ON sr.taskExecutionId = te.id
        JOIN daily_task dt ON te.dailyTaskId = dt.id
        WHERE dt.date = :date
    """)
    suspend fun totalRepsOnDate(date: String): Int?
}

@Dao
interface GrassDao {
    @Query("SELECT * FROM grass_cell WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    fun observeRange(from: String, to: String): Flow<List<GrassCellEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cell: GrassCellEntity)
}

@Dao
interface ConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(c: DailyConditionEntity)

    @Query("SELECT * FROM daily_condition WHERE date BETWEEN :from AND :to")
    suspend fun range(from: String, to: String): List<DailyConditionEntity>
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestone")
    fun observeAll(): Flow<List<MilestoneEntity>>

    @Update
    suspend fun update(m: MilestoneEntity)
}
```

## 4. 도메인 모델 (Mapper 대상)

```kotlin
// core-domain
data class UserProfile(
    val nickname: String,
    val gender: Gender,
    val birthYear: Int,
    val heightCm: Float,
    val weightKg: Float
)

enum class Gender { MALE, FEMALE, OTHER }

data class Goal(
    val id: Long,
    val category: GoalCategory,   // enum class { PULLUP }
    val targetValue: Int,
    val initialMaxReps: Int,
    val initialDeadHangSec: Int,
    val availableDays: Set<DayOfWeek>,
    val isActive: Boolean,
    val startedAt: Instant,
    val completedAt: Instant?
)

data class WeeklyPlan(
    val id: Long,
    val weekNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val phase: TrainingPhase,
    val targetMaxReps: Int,
    val totalVolume: Int,
    val dailyTasks: List<DailyTask>
)

enum class TrainingPhase { FOUNDATION, BUILD, PEAK, MAINTAIN }

data class DailyTask(
    val id: Long,
    val date: LocalDate,
    val dayType: DayType,         // WORKOUT / REST
    val intensity: Intensity,     // LIGHT / MODERATE / HARD / REST
    val summary: String,
    val executions: List<TaskExecution>,
    val isCompleted: Boolean
)

data class TaskExecution(
    val id: Long,
    val exerciseType: ExerciseType,
    val targetSets: Int,
    val targetReps: Int,
    val restSeconds: Int,
    val records: List<SetRecord>
)

enum class ExerciseType { PULLUP, ASSISTED_PULLUP, NEGATIVE, DEAD_HANG, AUSTRALIAN_PULLUP }

data class SetRecord(
    val id: Long,
    val setNumber: Int,
    val actualReps: Int,
    val actualSeconds: Int?,
    val recordedAt: Instant
)

data class GrassCell(
    val date: LocalDate,
    val intensityLevel: Int, // 0..4
    val totalReps: Int,
    val isWorkoutDay: Boolean,
    val isCompleted: Boolean
)
```

## 5. 마이그레이션 정책

- **Schema v1 (MVP 출시)**: 위 스키마
- **v2 이후**: 카테고리 추가 시 `Goal.category`에 새 enum이 들어가고, `ExerciseType`도 카테고리별로 확장. 기존 PULLUP 데이터는 그대로 유효.
- Room Migration은 add column만 사용하고 drop은 피한다 (사용자 데이터 보존이 최우선).

## 6. 예상 데이터 볼륨

| 항목 | 가정 | 12주 후 추정 |
|---|---|---|
| WeeklyPlan | 12 rows | 12 |
| DailyTask | 84 rows (12주×7일) | 84 |
| TaskExecution | 일당 평균 3종 × 운동일 36일 | ~108 |
| SetRecord | 운동일당 평균 9세트 × 36일 | ~324 |
| DailyCondition | 일당 1 row × 84 | 84 |
| GrassCell | 84 |

→ DB 크기: 1MB 미만 예상. 성능 이슈 없음.

## 7. 백업 / 내보내기 포맷 (JSON)

```json
{
  "version": "1.0",
  "exportedAt": "2026-05-06T10:00:00Z",
  "userProfile": { ... },
  "goals": [
    {
      "id": 1,
      "category": "PULLUP",
      "weeklyPlans": [ ... ]
    }
  ],
  "setRecords": [ ... ],
  "dailyConditions": [ ... ],
  "milestones": [ ... ]
}
```

설정 화면에서 사용자가 "내보내기" 버튼을 누르면 위 형식의 JSON을 SAF(Storage Access Framework)로 저장한다.
