# 마음먹은대로 (MaumMukeunDaero)

> *"나만 믿고 따라와. 그렇다면 네가 마음먹은, 네가 작성한 너의 작고 소중한 목표를 이루게 만들어 줄거야."*

## 프로젝트 한 줄 소개

작심한 목표를 **시뮬레이션 기반 단계적 계획 → 매일 피드백 → 시각적 성장 보상**의 사이클로 습관화시키는 안드로이드 앱.

## 왜 만드는가 (Problem)

대부분의 사람은 의지력이 부족해서 작심을 포기하는 게 아니다. **"내가 지금 어디쯤 와 있고, 어떤 경로로 가야 하며, 잘하고 있는지에 대한 즉각적 피드백"이 없을 때** 막연함을 느끼고 좌절한다. 시중의 습관 앱들은 대부분 "체크리스트 + 알림" 수준이다. 사용자의 현재 능력을 측정하고, 개인화된 경로를 제시하며, 매일 성장이 시각적으로 명확하게 보이는 앱은 드물다.

## 어떻게 해결하는가 (Solution)

1. **현재 능력 측정** — 사용자의 신체 정보(키, 몸무게)와 현재 수행 능력을 입력받음
2. **목표 시뮬레이션** — 운동 생리학에 기반한 알고리즘으로 목표 도달까지 개인화된 주차별/일자별 계획 자동 생성
3. **세부 Task 트래킹** — 오늘 해야 할 것과 전체 로드맵을 동시에 보여주는 UI
4. **시각적 성장 보상** — GitHub 잔디(Contribution Graph) 컨셉을 차용한 "성장 잔디"로 매일의 누적이 보이게 함
5. **피드백 루프** — 수행률·정체기·과부하를 자동 감지하고 다음 주차 계획을 동적으로 조정

## MVP 카테고리

**운동 / 턱걸이 10개 달성 프로젝트**

이 단일 시나리오를 깊게 만들어 검증한 뒤, 향후 카테고리를 확장한다 (러닝, 스쿼트, 학습, 독서, 식단 등).

## 환경

- **Target Platform**: Android (Galaxy 디바이스 우선 검증)
- **Development**: Windows 11 PC + Claude Code + GitHub (셸: Git Bash 또는 PowerShell)
- **Stage**: MVP 0.1.0

## 문서 목록

| # | 문서 | 내용 |
|---|------|------|
| 01 | [BUSINESS_PLAN.md](./docs/01_BUSINESS_PLAN.md) | 사업기획서 (비전, 시장, BM, 로드맵) |
| 02 | [PRODUCT_REQUIREMENTS.md](./docs/02_PRODUCT_REQUIREMENTS.md) | 제품 요구사항 명세 (PRD) |
| 03 | [TECHNICAL_ARCHITECTURE.md](./docs/03_TECHNICAL_ARCHITECTURE.md) | 기술 스택 및 아키텍처 |
| 04 | [DATA_MODEL.md](./docs/04_DATA_MODEL.md) | 데이터 모델 / DB 스키마 |
| 05 | [UI_UX_DESIGN.md](./docs/05_UI_UX_DESIGN.md) | 화면 설계 및 UX 플로우 |
| 06 | [DEVELOPMENT_ROADMAP.md](./docs/06_DEVELOPMENT_ROADMAP.md) | 개발 단계별 로드맵 |
| 07 | [CLAUDE_CODE_GUIDE.md](./docs/07_CLAUDE_CODE_GUIDE.md) | Claude Code 작업 가이드 (CLAUDE.md 원본) |
| 08 | [PULLUP_SIMULATION_LOGIC.md](./docs/08_PULLUP_SIMULATION_LOGIC.md) | 턱걸이 시뮬레이션 알고리즘 명세 |

## 프로젝트 구조 (예정)

```
maum-mukeun-daero/
├── README.md
├── CLAUDE.md                    # Claude Code 컨텍스트
├── docs/                        # 본 기획 문서들
├── android/                     # Android 앱 (Kotlin + Jetpack Compose)
│   ├── app/
│   ├── core/                    # 도메인 / 공통
│   ├── feature-onboarding/
│   ├── feature-plan/            # 시뮬레이션 / 계획
│   ├── feature-tracker/         # 일일 수행 트래킹
│   ├── feature-stats/           # 잔디 / 성장 시각화
│   └── data/                    # Room DB / 저장소
├── docs-design/                 # 디자인 시안 (Figma export 등)
└── scripts/                     # 빌드/배포 스크립트
```

## 시작하기

상세 작업 지침은 [CLAUDE_CODE_GUIDE](./docs/07_CLAUDE_CODE_GUIDE.md)를 참고하세요.

```powershell
# Windows (PowerShell 또는 Git Bash)
git clone https://github.com/<your-id>/maum-mukeun-daero.git
cd maum-mukeun-daero
claude   # Claude Code 실행
```

## 라이선스

추후 결정 (MVP 단계에서는 private repo 권장)
