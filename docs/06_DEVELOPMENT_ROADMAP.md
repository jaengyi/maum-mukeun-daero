# 06. 개발 로드맵 (Development Roadmap)

> Time horizon: 12 weeks to MVP
> Working style: 1인 개발 + Claude Code 페어 프로그래밍

---

## 1. 전체 단계 개요

| Phase | 기간 | 산출물 |
|---|---|---|
| **Phase 0**: 셋업 | Week 0~1 | 개발 환경, repo, CI 기초, 디자인 시안 |
| **Phase 1**: 코어 골격 | Week 2~3 | 멀티모듈 구조, Room DB, 네비게이션 |
| **Phase 2**: 온보딩 + 시뮬레이션 | Week 4~5 | 사용자 입력 → 계획 자동 생성 |
| **Phase 3**: 일일 트래커 | Week 6~7 | 홈, 운동 수행, 기록 저장 |
| **Phase 4**: 잔디 + 통계 | Week 8~9 | 잔디 그리드, 차트, 마일스톤 |
| **Phase 5**: 동적 재조정 + 알림 | Week 10 | 주간 리뷰, 자동 조정, 알림 |
| **Phase 6**: 폴리싱 + 베타 | Week 11~12 | 본인 베타 사용 + 피드백 반영 |

## 2. Phase 0: 셋업 (Week 0~1)

### 목표
개발 시작 전 모든 환경을 갖춘다.

### Tasks
- [ ] Windows 11에 JDK 17+ 설치 (`winget install EclipseAdoptium.Temurin.21.JDK`)
- [ ] Android Studio 설치 (`winget install Google.AndroidStudio`) — SDK Manager / adb / Compose Preview 포함
- [ ] SDK Platform 35, Build-Tools, Platform-Tools 설치 (Android Studio SDK Manager 또는 `sdkmanager` CLI)
- [ ] `ANDROID_HOME`, `JAVA_HOME` 환경 변수 등록 + `platform-tools` PATH
- [ ] GitHub repo 생성 (`maum-mukeun-daero`)
- [ ] 본 기획 문서 일체 commit
- [ ] `.gitignore` 작성 (Android 표준)
- [ ] `CLAUDE.md` 작성 (07 문서 기반)
- [ ] Galaxy 디바이스 ADB 연결 (USB 또는 Wi-Fi) 검증
- [ ] Figma 워크스페이스 개설 + 컬러/타이포 토큰 정리
- [ ] 앱 아이콘 초안 (간단한 잔디 모티브)

### 완료 기준
- `adb devices`로 디바이스 인식
- 빈 Compose 프로젝트가 디바이스에 설치되어 "Hello World" 표시

## 3. Phase 1: 코어 골격 (Week 2~3)

### 목표
앱의 뼈대 — 멀티모듈, DB, 네비게이션, 디자인 시스템 — 를 구축.

### Tasks

**모듈화**
- [ ] Gradle Kotlin DSL로 멀티모듈 셋업 (`settings.gradle.kts`)
- [ ] `app`, `core-common`, `core-domain`, `core-data`, `core-database`, `core-datastore`, `core-design`, `core-simulation` 모듈 생성
- [ ] `feature-onboarding`, `feature-plan`, `feature-tracker`, `feature-stats`, `feature-settings` 빈 모듈 생성
- [ ] Version Catalog (`libs.versions.toml`) 작성

**의존성 주입**
- [ ] Hilt 적용 (`@HiltAndroidApp`, `@AndroidEntryPoint`)
- [ ] DatabaseModule, RepositoryModule

**Room DB**
- [ ] 04 문서의 모든 Entity 작성
- [ ] 모든 DAO 인터페이스 작성
- [ ] `MmdDatabase` 클래스, Migration 자리 잡기
- [ ] DB schema export 활성화 (`schemas/` 디렉토리)
- [ ] DAO 단위 테스트 (in-memory DB)

**디자인 시스템 (core-design)**
- [ ] `MmdTheme` (Light + Dark)
- [ ] Color, Typography, Shape 토큰
- [ ] 기본 컴포넌트: `MmdButton`, `MmdCard`, `MmdTextField`, `MmdSegmentedControl`

**네비게이션**
- [ ] Navigation Compose 셋업
- [ ] 최상위 그래프: Onboarding 그래프 ↔ Main 그래프 (Bottom Nav)

### 완료 기준
- 빈 화면이지만 Bottom Nav 3탭 작동
- DB 마이그레이션 / DAO 테스트 그린

## 4. Phase 2: 온보딩 + 시뮬레이션 (Week 4~5)

### 목표
사용자가 정보를 입력하면 시뮬레이션된 12주 계획이 DB에 저장되는 사이클 완성.

### Tasks

**시뮬레이션 엔진 (core-simulation)**
- [ ] [08 문서](./08_PULLUP_SIMULATION_LOGIC.md)의 알고리즘 구현
- [ ] `PullupSimulator.generatePlan(input)` 구현
- [ ] 단위 테스트: 다양한 입력 (0개부터 8개까지)에서 합리적 결과 검증
- [ ] 엣지 케이스 테스트 (이미 10개 가능, 1주 3회만 가능 등)

**온보딩 화면들**
- [ ] S1 환영 화면
- [ ] S2 신체 정보 입력 (검증 포함)
- [ ] S3 능력 측정
- [ ] S4 운동 가능 요일
- [ ] S5 면책 동의
- [ ] 진행 인디케이터 (1/4, 2/4 ...)
- [ ] OnboardingViewModel + UiState

**시뮬레이션 결과 화면 (S6)**
- [ ] 시뮬레이션 호출 → 라인 차트 표시
- [ ] 마일스톤 리스트
- [ ] 강도 조절 옵션 (천천히 / 기본 / 빠르게)
- [ ] "이대로 시작하기" → DB에 Goal, WeeklyPlans, DailyTasks 일괄 저장

**Repository / UseCase**
- [ ] `UserProfileRepository`, `GoalRepository`, `PlanRepository`
- [ ] `CompleteOnboardingUseCase`, `GeneratePlanUseCase`

### 완료 기준
- 디바이스에서 온보딩 5화면 완주 가능
- DB에 12주치 데이터가 정상 저장 (DB Inspector로 확인)
- 시뮬레이션 단위 테스트 100% 그린

## 5. Phase 3: 일일 트래커 (Week 6~7)

### 목표
"오늘의 미션 → 운동 수행 → 완료 기록"의 전체 사이클 작동.

### Tasks

**홈 화면 (S7)**
- [ ] 오늘 날짜의 DailyTask 조회 → 운동일/휴식일 분기
- [ ] 미션 카드 (UI는 [05 문서](./05_UI_UX_DESIGN.md))
- [ ] 미니 잔디 (최근 4주)
- [ ] 휴식일 화면 (S7-b)

**운동 수행 화면 (S8)**
- [ ] 세트별 입력 UI (— / + 버튼)
- [ ] 휴식 타이머 (자동 시작)
- [ ] 진행 인디케이터 (●●○ ○ ○)
- [ ] 다음 운동 종목으로 자동 이동

**완료 화면 (S9)**
- [ ] 잔디 sprout 모션
- [ ] 컨디션 입력 (이모지 5단계)
- [ ] 메모 (옵션)
- [ ] 햅틱 피드백

**Repository / UseCase**
- [ ] `WorkoutRepository`, `ConditionRepository`
- [ ] `RecordSetUseCase`, `CompleteTodayUseCase`, `RecalcGrassCellUseCase`

### 완료 기준
- 오늘 운동 → 모든 세트 완료 → 잔디 칸 갱신까지 한 사이클 동작
- 강제 종료 후 재진입해도 진행 상태 복원

## 6. Phase 4: 잔디 + 통계 (Week 8~9)

### 목표
"내가 자라고 있다"가 시각적으로 명확한 성장 화면 완성.

### Tasks

**잔디 그리드 컴포넌트**
- [ ] `MmdGrassGrid` Composable (Canvas 기반)
- [ ] 12주 × 7요일 = 84칸 렌더링
- [ ] 색상 단계별 매핑 (intensity 0~4)
- [ ] 셀 탭 시 모달 (해당 일 기록 상세)
- [ ] 스크롤로 과거 기록 조회

**능력 성장 차트**
- [ ] Vico 또는 자체 Canvas 라인 차트
- [ ] 예상 곡선 (시뮬레이션) vs 실제 (기록 기반 추정)
- [ ] 주차별 마커

**마일스톤 시스템**
- [ ] 초기 데이터 시드 (FIRST_PULLUP, PULLUP_5, PULLUP_10, STREAK_7, STREAK_30)
- [ ] 세트 기록 시 마일스톤 달성 체크
- [ ] 달성 시 풀스크린 컨페티 애니메이션
- [ ] S10 화면에 마일스톤 리스트

**전체 계획 화면 (S11)**
- [ ] 주차별 카드 리스트 (확장 가능)
- [ ] 현재 주차 하이라이트
- [ ] 진행률 프로그레스 바

### 완료 기준
- 12주 잔디가 60fps로 부드럽게 렌더링
- 본인 사용 4주차에 첫 풀업 달성 시 마일스톤 모션 동작

## 7. Phase 5: 동적 재조정 + 알림 (Week 10)

### 목표
앱이 능동적으로 "지금 어떤 상태인지"를 알려주고 계획을 조정.

### Tasks

**주간 리뷰**
- [ ] 매주 일요일 21:00 WorkManager 실행
- [ ] 지난 주 수행률 / 평균 컨디션 분석
- [ ] 정체·과부하·순항 분기 로직 구현
- [ ] S12 모달 카드로 결과 표시
- [ ] "강도 조정" 수락 시 다음 주 계획 다시 생성

**알림**
- [ ] 일일 리마인더 AlarmManager
- [ ] 알림 권한 요청 (Android 13+)
- [ ] 정확 알람 권한 안내 (Android 12+)
- [ ] 알림 채널 (notification channel) 분리: 운동 / 리뷰 / 마일스톤
- [ ] 미수행 푸시 (저녁 19:00, 21:00 옵션)

**시뮬레이터 adjustPlan() 구현**
- [ ] `PullupSimulator.adjustPlan(currentPlan, progress, condition)` 구현
- [ ] 단위 테스트: 정체 시 강도 하향 / 순항 시 강도 유지/상향

### 완료 기준
- 주간 리뷰 카드 자동 생성 동작
- 사용자가 미션을 며칠 빠뜨리면 다음 주 계획이 자동으로 가벼워짐

## 8. Phase 6: 폴리싱 + 베타 (Week 11~12)

### 목표
출시 가능한 상태로 마감 + 본인 베타 사용으로 피드백 수집.

### Tasks

**설정 화면 (S13)**
- [ ] 프로파일 수정
- [ ] 알림 시간 변경
- [ ] 백업 / 복원 (JSON, SAF 사용)
- [ ] 전체 초기화 (확인 다이얼로그)
- [ ] 면책 고지 / 개인정보 처리방침 / 라이선스

**폴리싱**
- [ ] 모든 화면 다크모드 검증
- [ ] 접근성 검증 (TalkBack)
- [ ] 폰트 크기 변경 시 깨짐 없는지
- [ ] 가로/세로 회전 검증
- [ ] 리컴포지션 최소화 (Compose Compiler Metrics)

**문구 / 카피**
- [ ] 모든 문구 일관성 검토
- [ ] 빈 상태 / 에러 메시지 다듬기
- [ ] 면책 고지 / 개인정보 처리방침 작성

**아이콘 / 스플래시**
- [ ] 정식 앱 아이콘 (Adaptive Icon)
- [ ] Splash Screen API 적용

**테스트**
- [ ] 단위 테스트 커버리지: core-simulation 90%+, core-data 70%+
- [ ] UI 테스트 (Compose UI Test): 핵심 플로우 1개 이상
- [ ] 본인 베타 사용 4주 (실제 운동 데이터 누적)

**문서**
- [ ] README 갱신
- [ ] 변경 이력 (CHANGELOG.md)
- [ ] 빌드 / 디버그 가이드

### 완료 기준
- 본인이 4주간 사용하며 크래시 0건
- 본인 데이터로 잔디 / 차트가 의미 있게 보임
- Google Play 등록 가능한 상태 (스크린샷, 설명, 면책)

## 9. Phase 1 (출시 후) — 다음 우선순위

> 본 로드맵 이후 단계 (참고용)

- 클라우드 백업 (Firestore 또는 Supabase)
- 카테고리 확장: 푸시업, 스쿼트, 러닝
- 다중 활성 목표
- iOS (KMP)
- 위젯 (홈 화면 잔디 위젯)
- Wear OS (운동 중 워치에서 세트 카운트)
- AI 코칭 메시지 (LLM 통합) — 선택

## 10. 위험과 대비책

| 위험 | 대비책 |
|---|---|
| 시뮬레이션 알고리즘이 부정확 | 단위 테스트 + 본인 베타 데이터로 보정 |
| Compose 성능 이슈 (잔디) | Canvas로 직접 구현, 리컴포지션 최소화 |
| 안드로이드 도즈 / 배터리 | 정확 알람 + 사용자에게 배터리 최적화 예외 안내 |
| 일정 지연 | Phase 5 (동적 재조정)는 출시 후로 미룰 수 있음 — 정적 계획만으로도 출시 가능 |
| 본인 운동 부상 | 본인 베타 시 무리하지 말 것; 알고리즘 안전 마진 검증 |

## 11. 작업 시간 추정 (1인 기준)

| Phase | 코딩 | 디자인 | 테스트 | 합계 |
|---|---|---|---|---|
| 0 | 4h | 8h | 0h | ~12h |
| 1 | 16h | 8h | 4h | ~28h |
| 2 | 24h | 8h | 8h | ~40h |
| 3 | 24h | 4h | 8h | ~36h |
| 4 | 20h | 8h | 4h | ~32h |
| 5 | 16h | 4h | 8h | ~28h |
| 6 | 12h | 8h | 12h | ~32h |
| **합계** | | | | **~208h** |

> 주말 / 평일 저녁 기준 주 10~15시간 투자 시 약 14~20주 (~3~5개월). 위 12주 일정은 **풀 페이스 + Claude Code 적극 활용** 가정.
