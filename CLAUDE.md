# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

**마음먹은대로 (MaumMukeunDaero, MMD)** — 시뮬레이션 기반 계획, 일일 트래킹, 시각적 잔디 보상으로 목표를 습관화시키는 안드로이드 앱.

**MVP 범위**: "턱걸이 10개 달성" 단일 시나리오.

## Truth Source 문서

코드보다 문서가 우선. 새 결정이 필요하면 **먼저 문서를 업데이트하고 그 다음 코드를 변경**.

| 문서 | 용도 |
|------|------|
| `docs/02_PRODUCT_REQUIREMENTS.md` | 기능 요구사항 |
| `docs/03_TECHNICAL_ARCHITECTURE.md` | 모듈 구조, 기술 스택 |
| `docs/04_DATA_MODEL.md` | DB 스키마 |
| `docs/08_PULLUP_SIMULATION_LOGIC.md` | 시뮬레이션 알고리즘 |

기타 참고: `README.md`, `docs/05_UI_UX_DESIGN.md`, `docs/06_DEVELOPMENT_ROADMAP.md`

## 아키텍처

**Clean Architecture + MVVM** (멀티모듈 Gradle)

```
android/
├── app/                    # Application entry
├── core/
│   ├── core-common/        # 공통 유틸 (Result, Logger)
│   ├── core-domain/        # 도메인 모델, UseCase, Repository 인터페이스
│   ├── core-data/          # Repository 구현
│   ├── core-database/      # Room DB
│   ├── core-datastore/     # DataStore (설정값)
│   ├── core-design/        # 디자인 시스템 (Theme, Components)
│   └── core-simulation/    # 시뮬레이션 알고리즘 (순수 Kotlin)
└── feature/
    ├── feature-onboarding/
    ├── feature-plan/
    ├── feature-tracker/
    ├── feature-stats/
    └── feature-settings/
```

**의존성 방향**: `presentation → domain ← data` (역방향 금지)

**모듈별 의존성 규칙**:
- `feature-*` → `core-*` (단, `core-database`/`core-data` 직접 X — Repository 인터페이스만)
- `core-domain`, `core-simulation` → Android 의존성 0 (순수 Kotlin)

## 핵심 규칙

1. **모듈 추가, 라이브러리 추가, 스키마 변경** → 먼저 설명 후 승인받고 진행
2. **시뮬레이션 알고리즘 변경** → 08 문서 먼저 업데이트
3. **UI 텍스트**: 한국어, 친근한 존댓말, 죄책감 유발 카피 금지
4. **사용자 데이터(헬스, 기록)**: 외부 자동 전송 금지 (로컬 전용)

## 빌드 및 테스트 명령

```bash
# 빌드
./gradlew :app:assembleDebug

# 디바이스 설치
./gradlew :app:installDebug

# 전체 검사 + 테스트
./gradlew lint detekt test

# 특정 모듈 테스트
./gradlew :core:simulation:test
./gradlew :core:simulation:test --tests "PullupSimulatorTest"

# 코드 포맷팅
./gradlew ktlintFormat
```

## 코드 스타일

- **Kotlin 2.0+**, Jetpack Compose
- **Compose 패턴**: stateless composable + ViewModel state hoisting (`UiState`/`UiEvent` sealed class)
- 모든 외부 IO는 `suspend` 함수 또는 `Flow`
- import 와일드카드 금지
- 새 의존성 추가 시 `libs.versions.toml`에 등록
- DAO/Repository 작성 시 단위 테스트 함께 추가

## 환경

- 최소 SDK: **API 26** (Android 8.0)
- 타깃 SDK: **API 35**
- 타깃 디바이스: Galaxy (ADB USB/Wi-Fi 연결)

## 기본 결정

| 상황 | 기본 방침 |
|------|-----------|
| 새 화면 | `feature/<name>/` 모듈에 추가 |
| 새 데이터 저장 | Room Entity/DAO 추가 (DataStore는 단순 설정값만) |
| 외부 라이브러리 | 승인 필요 (라이선스, 크기, 활성도 확인) |
| 백엔드/클라우드 | MVP에서 금지 (로컬 우선) |

## 커밋 컨벤션

Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `chore:`, `refactor:`

예시:
```
feat(tracker): 오늘의 미션 카드 구현
fix(simulation): 0개 입력 시 NaN 발생 수정
```
