# 07. Claude Code 작업 가이드

> 본 문서의 핵심 부분은 repo 루트의 `CLAUDE.md`로 복사되어 Claude Code 세션마다 자동 컨텍스트가 됩니다.
> 작업하는 환경: **Linux 개인 서버 + Claude Code CLI + GitHub**

---

## 1. Claude Code란? (간단 정리)

Claude Code는 터미널에서 실행되는 에이전트형 코딩 도구로, 코드베이스를 읽고/쓰고 명령을 실행할 수 있습니다. 우리는 이 도구를 활용해 안드로이드 앱을 1인 + Claude의 페어 프로그래밍으로 진행합니다.

## 2. 사전 준비 (Linux 서버)

### 2.1 필수 도구
```bash
# JDK 17
sudo apt update
sudo apt install -y openjdk-17-jdk

# Node.js (Claude Code 설치용)
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Git
sudo apt install -y git

# Android SDK (cmdline-tools)
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip
mv cmdline-tools latest
```

> 위 cmdline-tools 다운로드 URL은 시점에 따라 바뀝니다. **Claude Code 작업 시작 시 최신 버전 확인**.

### 2.2 환경 변수 (`~/.bashrc` 또는 `~/.zshrc`)
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0
```

### 2.3 SDK 패키지 설치
```bash
sdkmanager --update
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"
sdkmanager --licenses   # 약관 동의
```

### 2.4 Claude Code 설치
```bash
npm install -g @anthropic-ai/claude-code
claude --version
```

> 설치 명령은 변경될 수 있으므로 [Anthropic Claude Code 공식 문서](https://docs.claude.com/) 확인 권장.

### 2.5 ADB로 Galaxy 디바이스 연결

**USB 연결**
1. Galaxy 폰 설정 → 휴대전화 정보 → 소프트웨어 정보 → 빌드번호 7회 탭 → 개발자 모드
2. 개발자 옵션 → USB 디버깅 ON
3. USB로 Linux 서버 연결
4. `adb devices`에 디바이스 ID 표시되면 OK

**Wi-Fi 연결 (Android 11+)**
```bash
# 폰: 개발자 옵션 → 무선 디버깅 ON → 페어링 코드로 기기 페어링
adb pair <폰IP>:<페어링포트>
adb connect <폰IP>:<연결포트>
adb devices
```

## 3. 프로젝트 초기화

```bash
mkdir -p ~/projects && cd ~/projects
git clone git@github.com:<your-id>/maum-mukeun-daero.git
cd maum-mukeun-daero

# 본 기획 문서들을 docs/ 에 그대로 commit (이미 했다면 skip)
git add docs/ README.md CLAUDE.md
git commit -m "docs: initial planning documents"
git push
```

## 4. Claude Code 실행 첫 흐름

```bash
cd ~/projects/maum-mukeun-daero
claude
```

세션이 열리면 다음과 같은 첫 프롬프트로 시작하면 좋습니다:

```
docs/ 안의 기획 문서들을 모두 읽어줘.
특히 03_TECHNICAL_ARCHITECTURE.md 의 모듈 구조와 06_DEVELOPMENT_ROADMAP.md 의 Phase 1 작업 목록을 우선 파악해줘.
파악이 끝나면 Phase 1의 첫 번째 작업인 멀티모듈 셋업을 위한 설계안을 먼저 보여줘.
구현은 내가 OK 한 뒤에 진행해.
```

## 5. CLAUDE.md (repo 루트에 둘 컨텍스트 파일)

다음 내용을 **`CLAUDE.md`**로 repo 루트에 만들어 두면 Claude Code가 모든 세션에서 자동으로 읽습니다.

```markdown
# CLAUDE.md — 마음먹은대로 (MMD) 프로젝트 컨텍스트

## 프로젝트 개요
마음먹은대로(MaumMukeunDaero)는 사용자의 작심한 목표를 시뮬레이션 기반 계획,
일일 트래킹, 시각적 잔디 보상으로 습관화시키는 안드로이드 앱이다.
MVP는 "턱걸이 10개 달성" 단일 시나리오에 집중한다.

## 필수 참고 문서 (이 순서로 읽을 것)
1. README.md
2. docs/01_BUSINESS_PLAN.md  - 비전과 BM
3. docs/02_PRODUCT_REQUIREMENTS.md  - 기능 요구사항 (truth)
4. docs/03_TECHNICAL_ARCHITECTURE.md  - 모듈 구조와 기술 스택 (truth)
5. docs/04_DATA_MODEL.md  - DB 스키마 (truth)
6. docs/05_UI_UX_DESIGN.md  - 화면 / UX
7. docs/06_DEVELOPMENT_ROADMAP.md  - 작업 순서
8. docs/08_PULLUP_SIMULATION_LOGIC.md  - 시뮬레이션 알고리즘 명세

## 절대 규칙
1. 위 문서들의 결정은 truth source. 새 결정이 필요하면 먼저 문서를 업데이트한다.
2. 모듈 의존성 방향을 어기지 않는다 (03 문서 §3.1).
3. 도메인 모델은 외부 의존성 없는 순수 Kotlin이다 (시뮬레이션 모듈도 마찬가지).
4. UI 텍스트는 한국어, 친근한 존댓말 (05 문서 §7).
5. 사용자 데이터(헬스, 기록)는 절대 외부로 자동 전송하지 않는다.
6. 큰 변경 전에 의도와 영향 범위를 먼저 설명하고 승인받는다.

## 코드 스타일
- Kotlin 2.0+, Compose, MVVM, Clean Architecture
- Compose: stateless composable + ViewModel state hoisting
- 함수 단위 작게, 단일 책임
- 모든 외부 IO는 suspend 함수 또는 Flow
- ktlint / detekt 통과 필수

## 파일 작성 시
- 새 파일 생성 시 같은 모듈의 기존 패턴을 따른다
- import 정리 (와일드카드 X)
- DAO/Repository 작성 시 단위 테스트도 함께

## 빌드 / 실행
- `./gradlew :app:assembleDebug`  - 디버그 빌드
- `./gradlew :app:installDebug`   - 디바이스 설치
- `./gradlew lint detekt test`    - 검사 + 테스트

## 작업 흐름
1. 작업 시작 시: 어떤 Phase / 어떤 Task 인지 명시
2. 작업 중: 단계별 진행 보고
3. 작업 후: 변경 파일 요약 + 다음 단계 제안
```

> 위 CLAUDE.md를 `/home/claude/maum-mukeun-daero/CLAUDE.md` 로 저장하세요 (이 문서 끝에 별도 파일로도 만들어 둡니다).

## 6. Claude Code와 효과적으로 일하기 위한 패턴

### 6.1 작업 시작 패턴
```
[Phase 2 / Task: 시뮬레이션 엔진]
지금부터 core-simulation 모듈의 PullupSimulator 를 만들어보자.
docs/08_PULLUP_SIMULATION_LOGIC.md 의 §3 ~ §6 알고리즘을 그대로 구현해줘.
구현 전에:
1) 인터페이스 시그니처
2) 내부 데이터 흐름
3) 단위 테스트 케이스 목록
을 먼저 보여주고, 내가 OK 하면 코딩 시작해.
```

### 6.2 절대 하면 안 되는 패턴
- ❌ "알아서 다 해줘" — 컨텍스트가 모호하면 추측을 많이 하게 됨
- ❌ 한 번에 여러 모듈을 동시에 변경시키기
- ❌ 테스트 없이 큰 알고리즘 작성

### 6.3 권장 패턴
- ✅ 한 번에 하나의 작업 + 명확한 완료 조건
- ✅ "구현 전에 설계안부터 보여줘" 항상 먼저
- ✅ 매 단계 git commit (Conventional Commits)
- ✅ Claude가 모르는 외부 사실(라이브러리 최신 버전 등)은 web search 활용 요청

### 6.4 자주 쓰는 명령 모음

```bash
# 빌드
./gradlew assembleDebug
./gradlew :feature:tracker:assembleDebug   # 특정 모듈만

# 디바이스 설치 + 실행
./gradlew installDebug && adb shell am start -n com.mmd/.MainActivity

# 로그 보기 (앱 태그만)
adb logcat -s "MMD:*" "AndroidRuntime:E"

# Compose 컴파일러 메트릭
./gradlew assembleRelease -PenableComposeCompilerReports=true

# 테스트
./gradlew test
./gradlew :core:simulation:test --tests "PullupSimulatorTest"

# Lint / 정적 분석
./gradlew lint
./gradlew detekt
./gradlew ktlintFormat
```

## 7. 디렉토리 / 파일 컨벤션

### 7.1 패키지명
- 베이스: `com.mmd`
- 모듈별: `com.mmd.feature.tracker`, `com.mmd.core.simulation`, ...

### 7.2 파일명
- `PullupSimulator.kt` (인터페이스 + 구현이 같이 있을 때는 같은 파일에)
- `PullupSimulatorImpl.kt` (분리할 만큼 크면)
- ViewModel: `<Screen>ViewModel.kt`
- UiState: 같은 파일 또는 `<Screen>UiState.kt`
- Composable Screen: `<Name>Screen.kt`

### 7.3 테스트 파일 위치
```
core/simulation/
├── src/main/kotlin/com/mmd/core/simulation/PullupSimulator.kt
└── src/test/kotlin/com/mmd/core/simulation/PullupSimulatorTest.kt
```

## 8. 커밋 컨벤션 (Conventional Commits)

```
feat(tracker): 오늘의 미션 카드 구현
fix(simulation): 0개 입력 시 NaN 발생 수정
chore(deps): Compose BOM 2024.10.01 로 업데이트
docs(prd): 잔디 색상 단계 정의 보강
test(simulation): 정체기 감지 케이스 추가
refactor(data): GoalRepository 인터페이스 정리
```

타입: `feat`, `fix`, `chore`, `docs`, `test`, `refactor`, `style`, `perf`, `build`, `ci`

## 9. 브랜치 / PR 흐름

```bash
# 새 작업 시작
git switch -c feature/onboarding-screens

# 작업 + 커밋 (의미 있는 단위로 자주)
git add . && git commit -m "feat(onboarding): S2 신체 정보 입력 화면 구현"

# 푸시 + PR
git push -u origin feature/onboarding-screens
# (GitHub UI에서 PR 생성 → develop 으로 머지)
```

> 1인 개발이지만 PR 단위는 유지 — 변경 이력을 깔끔하게 관리하기 위해.

## 10. 자주 부딪힐 이슈 미리보기

| 이슈 | 해결 |
|---|---|
| Gradle sync가 SDK 라이선스 문제로 실패 | `sdkmanager --licenses` 다시 실행 |
| Galaxy에서 알림이 안 뜸 | 설정 → 배터리 → 앱 절전 예외 추가 |
| Compose 리컴포지션 과다 | Compose Compiler Metrics로 unstable 클래스 찾아 `@Stable` 또는 `data class` 분리 |
| Room schema 변경 후 빌드 실패 | `schemas/` 디렉토리 비우고 재빌드, 또는 Migration 작성 |
| ADB Wi-Fi 끊김 | 폰 재페어링, 또는 USB로 fallback |

## 11. 보안 / 비밀 관리

- **Keystore**: `keystore/` 디렉토리에 두고 `.gitignore`에 포함
- **API 키**: 현재 MVP는 외부 API 없음. 추후 추가 시 `local.properties` 사용
- **`.env` / 비밀 파일**: 절대 commit 금지. pre-commit hook 권장 (gitleaks 등)
- Claude Code에게 비밀 값을 직접 입력하지 말 것 (필요 시 환경변수로 주입)

## 12. 권장 작업 시간 / 세션 길이

- 한 세션 90~120분 권장 (Claude Code 대화 컨텍스트가 너무 길어지면 정확도 하락)
- 한 세션에서 한 모듈 / 한 화면 단위로 마무리하고 commit + push
- 다음 세션 시작 시 "지난 세션에서 X를 했고 다음은 Y" 한 줄로 컨텍스트 재진입

## 13. 첫 한 주 추천 작업 순서

```
Day 1: 환경 셋업 + repo 생성 + 기획 문서 commit + CLAUDE.md 작성
Day 2: 멀티모듈 빈 골격 (모든 모듈 폴더 + build.gradle.kts)
Day 3: Hilt + Compose + Navigation 셋업, "Hello MMD" 화면
Day 4: Room DB Entity 5개 + DAO 작성 + 단위 테스트
Day 5: 디자인 시스템 토큰 (Color, Typography, Theme)
Day 6: 온보딩 S1, S2 화면 (Compose만, 데이터 연동 X)
Day 7: 한 주 회고 + Phase 2 시작 준비 + git push
```

## 14. 참고 링크 (수시 확인)

- Anthropic Claude Code 문서: https://docs.claude.com/
- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose
- Kotlin: https://kotlinlang.org/docs/home.html
- Room: https://developer.android.com/training/data-storage/room
- Hilt: https://developer.android.com/training/dependency-injection/hilt-android

> 위 링크의 내용은 수시로 업데이트되므로, 코드 작성 직전 Claude Code에게 "최신 권장 방식인지 확인해 달라"고 요청하면 좋습니다.
