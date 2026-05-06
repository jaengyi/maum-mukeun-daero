# CLAUDE.md — 마음먹은대로 (MMD) 프로젝트 컨텍스트

> 이 파일은 Claude Code가 모든 세션에서 자동으로 읽는 컨텍스트 문서입니다.
> 프로젝트 루트에 위치합니다.

## 프로젝트 개요

**마음먹은대로 (MaumMukeunDaero, MMD)** 는 사용자의 작심한 목표를 시뮬레이션 기반 계획, 일일 트래킹, 시각적 잔디 보상으로 습관화시키는 안드로이드 앱입니다.

**MVP 범위**: "턱걸이 10개 달성" 단일 시나리오에 집중.

## 필수 참고 문서 (이 순서로 읽으세요)

1. `README.md` — 프로젝트 개요
2. `docs/01_BUSINESS_PLAN.md` — 비전과 BM
3. `docs/02_PRODUCT_REQUIREMENTS.md` — 기능 요구사항 (truth source)
4. `docs/03_TECHNICAL_ARCHITECTURE.md` — 모듈 구조와 기술 스택 (truth source)
5. `docs/04_DATA_MODEL.md` — DB 스키마 (truth source)
6. `docs/05_UI_UX_DESIGN.md` — 화면 / UX 설계
7. `docs/06_DEVELOPMENT_ROADMAP.md` — 작업 순서
8. `docs/07_CLAUDE_CODE_GUIDE.md` — 작업 가이드
9. `docs/08_PULLUP_SIMULATION_LOGIC.md` — 시뮬레이션 알고리즘 명세 (truth source)

## 절대 규칙

1. **문서가 truth source.** 위 문서들의 결정과 충돌하는 새 결정이 필요하면, **먼저 문서를 업데이트하고 그 다음 코드를 변경**합니다.
2. **모듈 의존성 방향 준수**: `presentation → domain ← data`. 절대 역방향 의존성 금지. 03 문서 §3.1 표 참조.
3. **도메인 / 시뮬레이션은 순수 Kotlin**: `core-domain`, `core-simulation` 모듈은 Android 의존성 0.
4. **UI 텍스트는 한국어, 친근한 존댓말** (05 문서 §7 참조). 죄책감 유발 카피 금지.
5. **사용자 데이터(헬스, 기록)는 절대 외부로 자동 전송 금지.**
6. **큰 변경 전 승인**: 모듈 추가, 라이브러리 추가, 스키마 변경 등은 의도와 영향 범위를 먼저 설명하고 사용자 OK를 받은 후 진행.
7. **시뮬레이션 알고리즘 변경 시 08 문서 먼저 업데이트.**

## 코드 스타일

- Kotlin 2.0+, Jetpack Compose, MVVM, Clean Architecture
- Compose: stateless composable + ViewModel state hoisting (UiState/UiEvent 패턴)
- 함수는 작게, 단일 책임
- 모든 외부 IO는 `suspend` 함수 또는 `Flow`
- ktlint / detekt 통과 필수
- import 와일드카드 금지

## 파일 작성 시 체크리스트

- [ ] 같은 모듈의 기존 패턴을 따랐는가?
- [ ] 새 패키지를 만들 필요가 정말 있는가?
- [ ] DAO/Repository를 작성했다면 단위 테스트도 함께 추가했는가?
- [ ] 새 의존성을 추가했다면 `libs.versions.toml`에 등록했는가?

## 빌드 / 실행 명령

```bash
./gradlew :app:assembleDebug      # 디버그 빌드
./gradlew :app:installDebug       # 디바이스 설치
./gradlew lint detekt test        # 검사 + 테스트
./gradlew :core:simulation:test   # 시뮬레이션 모듈만 테스트
```

## 작업 흐름 (매 세션)

1. **시작 시**: 어떤 Phase / 어떤 Task인지 명시 ("Phase 2 / 시뮬레이션 엔진 구현")
2. **진행 중**: 단계별로 진행 보고. 큰 작업은 설계안을 먼저 보여주고 승인 후 코딩.
3. **종료 시**:
   - 변경된 파일 요약
   - 다음 단계 제안
   - Conventional Commit 메시지 제안 (`feat:`, `fix:`, `docs:`, `test:`, `chore:`, `refactor:`)

## 환경

- 개발 머신: **Linux 개인 서버**
- 타깃 디바이스: **Galaxy 안드로이드 폰** (ADB로 연결)
- VCS: **GitHub**
- 최소 SDK: **Android 8.0 (API 26)**
- 타깃 SDK: **API 35**

## 자주 마주칠 결정에 대한 기본값

- 새 화면이 필요하면 → `feature/<name>/` 모듈에 추가
- 새 데이터 저장이 필요하면 → Room Entity / DAO 추가, DataStore는 단순 설정값만
- 외부 라이브러리는 **사용자 승인 필요** (라이선스, 크기, 활성도 확인)
- 백엔드 / 클라우드는 **MVP에서 도입 금지** (로컬 우선)

## 모르는 것이 있으면

- 외부 라이브러리 최신 버전, 안드로이드 정책 변경 등은 web search 활용
- 결정이 어렵다면 사용자에게 옵션 2~3개와 trade-off를 제시하고 선택을 요청
