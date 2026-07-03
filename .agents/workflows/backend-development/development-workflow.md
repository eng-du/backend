---
description: development workflow
---

# Backend Development Workflow

## Purpose

Use this workflow for Java/Spring backend feature development.

This workflow coordinates phase-specific skills.
Do not use the default Antigravity planning format.

## Language Rule

All artifacts, summaries, reviews, and explanations must be written in Korean.

Keep code, package names, class names, method names, variable names, API paths, and SQL in English.

## Artifact Rule

Artifacts are for human review.
Each artifact must be readable within five minutes.

## Execution Order Rule

각 페이즈의 결과물은 반드시 아티팩트 형태로 사용자에게 제출하고 리뷰를 받아야 한다.

작업은 반드시 아래 순서로만 진행한다.

Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5

이전 페이즈의 아티팩트에 대한 명시적 승인 없이 다음 페이즈로 진행할 수 없다.

## Phases

### Phase 1. Design Review

Use skill:

`.agents/skills/design-review/design-review-skill.md`

Output:

`설계 검토서`

Gate:

Stop after this phase and wait for explicit user approval.

---

### Phase 2. Test Writing

Use skill:

`.agents/skills/test-writing/test-writing-skill.md`

Input:

Approved `설계 검토서`

Output:

`테스트 코드 검토서`

Gate:

Stop after this phase and wait for explicit user approval.

---

### Phase 3. Core Review

Use skill:

`.agents/skills/core-review/core-review-skill.md`

Input:

Approved `설계 검토서`

Approved `테스트 코드 검토서`

Output:

`핵심 구현 검토서`

Gate:

Stop after this phase and wait for explicit user approval.

---

### Phase 4. Implementation

Use skill:

`.agents/skills/implementation/implementation-skill.md`

Input:

Approved `설계 검토서`

Approved `테스트 코드 검토서`

Approved `핵심 구현 검토서`

Output:

`구현 결과서`

Gate:

Stop after this phase and present the 구현 결과서 to the user.

---

### Phase 5. Final User Review

Output a concise Korean summary for the user.

Include:

- 변경 파일
- 핵심 로직 요약
- 테스트 / 빌드 결과
- 남은 리스크
- 사용자가 결정해야 할 사항