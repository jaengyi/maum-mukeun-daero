# 03. 기술 아키텍처 (Technical Architecture)

> Target Platform: Android (Native, Kotlin)
> Architecture Pattern: Clean Architecture + MVVM
> Build System: Gradle (Kotlin DSL)

---

## 1. 기술 스택

### 1.1 핵심 스택

| 영역 | 선택 | 이유 |
|---|---|---|
| **언어** | Kotlin 2.0+ | 안드로이드 1st-class 언어, Null safety, Coroutine |
| **UI 프레임워크** | Jetpack Compose | 선언적 UI, 모던 안드로이드 표준, 애니메이션 용이 |
| **최소 SDK** | API 26 (Android 8.0) | 국내 사용자 99% 이상 커버 |
| **타깃 SDK** | API 35 (Android 15) | 최신 정책 준수 |
| **빌드 도구** | Gradle (Kotlin DSL) | 표준 |
| **DI** | Hilt | 보일러플레이트 적음, 안드로이드 특화 |
| **비동기** | Kotlin Coroutines + Flow | 표준, Compose와 자연스럽게 결합 |
| **로컬 DB** | Room | SQLite 추상화, Flow 지원 |
| **DataStore** | Jetpack DataStore (Preferences) | SharedPreferences 대체, 비동기 안전 |
| **네비게이션** | Navigation Compose | Compose 표준 |
| **차트** | Vico (또는 자체 Canvas) | 가벼움, Compose 친화 |
| **이미지** | Coil | Compose 표준 |
| **알림 / 백그라운드** | WorkManager + AlarmManager | 일일 알림 + 주간 리뷰 |
| **테스트** | JUnit5, MockK, Compose UI Test | 표준 |
| **로깅** | Timber | 디버그/릴리즈 분리 |

### 1.2 보류 / 검토 대상

| 항목 | 검토 시점 | 비고 |
|---|---|---|
| KMP (Kotlin Multiplatform) | Phase 1 말 | iOS 확장 시점 |
| SQLCipher | MVP 후반 | 헬스 데이터 암호화 필요 시 |
| Firebase Crashlytics | Phase 1 | 사용자 동의 게이트 후 |
| Sentry | Phase 1 | Crashlytics 대안 |

## 2. 아키텍처 전반 (Clean Architecture)

```
┌──────────────────────────────────────────────────────────────┐
│                       Presentation Layer                     │
│      (Compose UI + ViewModel + UiState + UiEvent)            │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                          Domain Layer                        │
│   (UseCase, Domain Model, Repository Interface, Pure Logic)  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                          Data Layer                          │
│   (Repository Impl, Room DAO, DataStore, Local DataSources)  │
└──────────────────────────────────────────────────────────────┘
```

**의존성 방향**: Presentation → Domain ← Data
**규칙**: Domain은 외부 의존성 없는 순수 모듈, Data와 Presentation은 Domain의 인터페이스만 안다.

## 3. 모듈 구성 (Multi-module Gradle)

```
maum-mukeun-daero/
└── android/
    ├── app/                              # Application module (entry)
    ├── core/
    │   ├── core-common/                  # 공통 유틸 (Result, Logger 등)
    │   ├── core-domain/                  # 도메인 모델 + UseCase 인터페이스
    │   ├── core-data/                    # Repository 구현
    │   ├── core-database/                # Room DB
    │   ├── core-datastore/               # DataStore (설정값)
    │   ├── core-design/                  # 디자인 시스템 (Color, Typo, Theme, Components)
    │   └── core-simulation/              # 시뮬레이션 알고리즘 (순수 로직)
    └── feature/
        ├── feature-onboarding/
        ├── feature-plan/                 # 시뮬레이션 결과 / 전체 계획 보기
        ├── feature-tracker/              # 홈 + 일일 수행
        ├── feature-stats/                # 잔디 + 차트
        └── feature-settings/
```

### 3.1 모듈별 의존성 규칙

| 모듈 | 의존 가능 |
|---|---|
| `app` | 모든 feature, core |
| `feature-*` | core-* (단, core-database / core-data 직접 X — Repository 인터페이스만) |
| `core-data` | core-domain, core-database, core-datastore |
| `core-domain` | core-common, core-simulation |
| `core-simulation` | (없음, 순수 Kotlin) |
| `core-database` | core-domain (Entity↔Domain 매퍼용) |
| `core-design` | (없음, Compose만) |

이렇게 분리하면 시뮬레이션 알고리즘은 단위 테스트가 쉽고, 추후 Kotlin Multiplatform 모듈로 분리 가능하다.

## 4. 화면 단위 아키텍처 (MVVM)

```
┌─────────────────────┐    UiEvent    ┌─────────────────────┐
│   Composable        │ ────────────▶ │     ViewModel       │
│   (View)            │               │                     │
│                     │ ◀──────────── │                     │
└─────────────────────┘    UiState    └─────────────────────┘
                                              │
                                              │ uses
                                              ▼
                                       ┌─────────────────┐
                                       │     UseCase     │
                                       └─────────────────┘
                                              │
                                              ▼
                                       ┌─────────────────┐
                                       │   Repository    │
                                       │   (Interface)   │
                                       └─────────────────┘
                                              │
                                              ▼
                                       ┌─────────────────┐
                                       │ RepositoryImpl  │
                                       │ + Room DAO      │
                                       └─────────────────┘
```

### 4.1 표준 패턴

**UiState (sealed/단일 data class)**
```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val today: TodayMission? = null,
    val errorMessage: String? = null
)
```

**UiEvent (sealed)**
```kotlin
sealed interface HomeUiEvent {
    data object OnStartWorkoutClicked : HomeUiEvent
    data class OnSetCompleted(val setId: Long, val reps: Int) : HomeUiEvent
}
```

**ViewModel**: `StateFlow<UiState>` 노출, `onEvent(event)` 단일 진입점.

## 5. 데이터 흐름 시나리오

### 5.1 "오늘의 미션 표시" 흐름

```
HomeScreen (Composable)
   └─ collectAsStateWithLifecycle(homeViewModel.uiState)
       └─ ViewModel.init { observeTodayMission() }
           └─ getTodayMissionUseCase()
               └─ planRepository.observePlanForToday(today: LocalDate)
                   └─ planDao.getPlanForDate(date) : Flow<PlanEntity?>
                       └─ Mapper(Entity → Domain)
```

### 5.2 "세트 완료 기록" 흐름

```
TrackerScreen.onSetComplete(reps)
   └─ ViewModel.onEvent(OnSetCompleted(setId, reps))
       └─ recordSetUseCase(setId, reps, timestamp)
           ├─ workoutRepository.saveSetRecord(record)
           │     └─ workoutDao.insertSetRecord(entity) [@Transaction]
           └─ growthRepository.recalcDayCell(date)
               └─ growthDao.upsertCell(cellEntity)
   └─ UiState 업데이트 → 잔디 애니메이션 트리거
```

## 6. 시뮬레이션 엔진 (core-simulation)

### 6.1 격리

시뮬레이션은 **순수 Kotlin 모듈**로 분리한다. 안드로이드 의존성 없음. 단위 테스트 100% 가능.

### 6.2 인터페이스

```kotlin
// core-simulation
interface PullupSimulator {
    fun generatePlan(input: SimulationInput): SimulationResult
    fun adjustPlan(
        currentPlan: SimulationResult,
        progress: ProgressLog,
        condition: ConditionLog
    ): SimulationResult
}

data class SimulationInput(
    val heightCm: Int,
    val weightKg: Float,
    val age: Int,
    val gender: Gender,
    val currentMaxPullups: Int,
    val currentDeadHangSeconds: Int,
    val availableDaysOfWeek: Set<DayOfWeek>,
    val targetReps: Int = 10
)

data class SimulationResult(
    val totalWeeks: Int,
    val weeklyPlans: List<WeeklyPlan>,
    val expectedMilestones: List<Milestone>
)
```

상세는 [08_PULLUP_SIMULATION_LOGIC.md](./08_PULLUP_SIMULATION_LOGIC.md) 참조.

## 7. 백그라운드 작업

| 작업 | 도구 | 트리거 |
|---|---|---|
| 일일 운동 리마인더 | AlarmManager (정확한 시각) | 사용자 설정 시각 |
| 미수행 푸시 (저녁) | AlarmManager | 19:00, 21:00 (옵션) |
| 주간 리뷰 생성 | WorkManager (PeriodicWorkRequest) | 매주 일요일 21:00 |
| DB 정리 (180일 초과 로그) | WorkManager | 주 1회 |

## 8. 알림 권한 / OS 정책

- **Android 13+**: `POST_NOTIFICATIONS` 런타임 권한 필요 → 온보딩 마지막 단계에서 요청
- **Android 12+**: 정확 알람 권한 (`SCHEDULE_EXACT_ALARM`) — Galaxy 노티 시간 정확도 위해 필요시 요청
- **Doze / 배터리 최적화**: 사용자에게 안내 (Galaxy 기본은 백그라운드 제한이 강함)

## 9. 디자인 시스템 (core-design)

### 9.1 컬러 팔레트 (예시 — 추후 시안 확정)

| 토큰 | Light | Dark | 용도 |
|---|---|---|---|
| `primary` | #2E7D32 (성장 그린) | #66BB6A | 주요 액션 |
| `onPrimary` | #FFFFFF | #003300 | 주요 액션 텍스트 |
| `surface` | #FFFFFF | #121212 | 카드 배경 |
| `grass-0` | #EBEDF0 | #161B22 | 잔디 빈칸 |
| `grass-1` | #9BE9A8 | #0E4429 | 잔디 1단계 |
| `grass-2` | #40C463 | #006D32 | 잔디 2단계 |
| `grass-3` | #30A14E | #26A641 | 잔디 3단계 |
| `grass-4` | #216E39 | #39D353 | 잔디 4단계 |

> 잔디 색상은 GitHub 컨트리뷰션 그래프 색상 참고 (라이트 / 다크 두 세트)

### 9.2 타이포그래피
- 시스템 폰트 (San Francisco / Pretendard 검토)
- 기본 16sp, 헤딩 22~28sp

### 9.3 컴포넌트
- `MmdButton`, `MmdCard`, `MmdMissionCard`, `MmdGrassGrid`, `MmdProgressLine` 등
- 모두 `core-design`에 위치, feature 모듈에서 재사용

## 10. 개발·배포 환경

### 10.1 개발 머신
- **Linux 개인 서버**: 코드/빌드/Claude Code 작업 호스트
- **로컬 디바이스 (Galaxy)**: ADB over Wi-Fi 또는 USB로 디바이스 테스트
- **에뮬레이터**: 보조 (정확한 잔디 색감·햅틱 검증은 실기기)

### 10.2 빌드 / CI
- 로컬: `./gradlew assembleDebug`
- GitHub Actions (옵션, Phase 1):
  - PR 시 lint, ktlint, detekt, unit test
  - main 브랜치 머지 시 release artifact 빌드 (서명은 별도 비공개 브랜치)

### 10.3 코드 품질 도구
- **ktlint**: 포맷
- **detekt**: 정적 분석
- **Compose Compiler Metrics**: 리컴포지션 감시

### 10.4 브랜치 전략
- `main`: 안정 / 출시 가능
- `develop`: 다음 릴리즈 통합
- `feature/<name>`: 기능 브랜치
- 커밋 컨벤션: Conventional Commits (`feat:`, `fix:`, `chore:` ...)

## 11. 보안·프라이버시 처리

- 헬스 데이터(키, 몸무게, 신체 능력)는 로컬 Room DB에만 저장
- DB 암호화: MVP는 미적용, Phase 1에서 SQLCipher 검토
- 백업 파일(JSON 내보내기)은 사용자가 명시적으로 요청해야 생성됨
- 분석 SDK / 광고 SDK 없음 (MVP)

## 12. 디렉토리 구조 (실제 파일 트리 예시)

```
android/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/mmd/
│       │   ├── MmdApplication.kt
│       │   └── MainActivity.kt
│       └── res/
├── core/
│   ├── common/
│   │   └── src/main/kotlin/com/mmd/core/common/
│   ├── domain/
│   │   └── src/main/kotlin/com/mmd/core/domain/
│   │       ├── model/
│   │       ├── repository/      # 인터페이스
│   │       └── usecase/
│   ├── data/
│   │   └── src/main/kotlin/com/mmd/core/data/
│   │       └── repository/      # 구현
│   ├── database/
│   │   └── src/main/kotlin/com/mmd/core/database/
│   │       ├── MmdDatabase.kt
│   │       ├── dao/
│   │       └── entity/
│   ├── datastore/
│   ├── design/
│   │   └── src/main/kotlin/com/mmd/core/design/
│   │       ├── theme/
│   │       └── component/
│   └── simulation/
│       └── src/main/kotlin/com/mmd/core/simulation/
│           ├── PullupSimulator.kt
│           └── internal/        # 알고리즘 세부
└── feature/
    ├── onboarding/
    ├── plan/
    ├── tracker/
    ├── stats/
    └── settings/
```

## 13. 외부 의존성 (예상 라이브러리 목록)

```toml
[versions]
kotlin = "2.0.21"
agp = "8.6.1"
compose-bom = "2024.10.01"
hilt = "2.52"
room = "2.6.1"
datastore = "1.1.1"
navigation-compose = "2.8.3"
coroutines = "1.9.0"
work-manager = "2.9.1"
timber = "5.0.1"

[libraries]
# Compose
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
compose-ui = { module = "androidx.compose.ui:ui" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }

# Hilt
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-android-compiler", version.ref = "hilt" }

# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }

# DataStore
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }

# Navigation
navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigation-compose" }

# Coroutines
coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }

# WorkManager
work-runtime-ktx = { module = "androidx.work:work-runtime-ktx", version.ref = "work-manager" }

# Logging
timber = { module = "com.jakewharton.timber:timber", version.ref = "timber" }
```

> 위 버전은 작성 시점 기준이며 Claude Code 작업 시 최신 안정 버전 확인 필요.

## 14. 시작 전 체크리스트

- [ ] Linux 개발 서버에 JDK 17 설치
- [ ] Android Studio (또는 SDK + cmdline-tools) 설치
- [ ] Android SDK Platform 35, Build-Tools 35.x
- [ ] Galaxy 디바이스 USB 디버깅 활성화 (또는 ADB Wi-Fi)
- [ ] GitHub repo 생성 + SSH 키 등록
- [ ] Claude Code CLI 설치 + 인증
- [ ] 작성한 본 문서 일체를 repo의 `docs/`에 commit
