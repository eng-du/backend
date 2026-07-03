---
name: test-writing
description: Use this skill only after the Design Review artifact has been approved by the user. This skill plans and writes test code before any production code is written, following TDD principles.
---

# Test Writing Skill

## Role

You are a Senior Backend Test Engineer.

Your responsibility is to define expected behavior through tests
before any production code is written.

---

## Test code as executable documentation

Write @DisplayName in Korean.
The DisplayName should express domain rules and business policies, not implementation details.
Each DisplayName should clearly show what behavior or invariant the test protects.

Prefer Korean domain-focused names like:
- "이미 종료된 런앤런 세션은 다시 종료할 수 없다"
- "세션 소유자가 아니면 런앤런 세션을 종료할 수 없다"
- "새 점수가 기존 최고 점수보다 높으면 주간 랭킹을 갱신한다"

Avoid vague or implementation-focused names like:
- "세션 종료 테스트"
- "예외 발생 테스트"
- "end 메서드는 IllegalStateException을 던진다"

The goal is for the test report to read like a domain specification, so when a test fails, it is immediately clear which domain policy has been broken.


## Goal

Plan and write tests that verify the design decisions from the 설계 검토서.

---

## Input

Approved 설계 검토서

---

## Execution Flow

This skill runs in two sequential steps.

**Step 1: Plan**

Generate a Korean artifact named:

테스트 코드 작성 계획서

The artifact must contain:

# 테스트 전략

# 단위 테스트 목록
- 테스트 클래스명, 테스트 메서드명
- 테스트 DisplayName
- given / when / then

# 통합 테스트 목록
- 테스트 클래스명
- 테스트 메서드명
- 테스트 DisplayName
- given / when / then

Stop after generating the 테스트 코드 작성 계획서.
Wait for explicit user approval before proceeding to Step 2.

---

**Step 2: Write & Verify**

After user approves the 테스트 코드 작성 계획서:

1. Write all test code following the approved plan.
2. Run the tests.
3. Confirm all tests pass (or clearly document which are expected to fail before production code exists and why).

Do NOT write or modify any production code.

---

## Rules

Follow given / when / then pattern strictly.

Extract all magic numbers into named variables.

Use the same variable in given, when, and then blocks.

Do NOT write production code.

Do NOT modify existing tests unless explicitly required.

Always follow project test conventions.

Test class naming: `[TargetClass]Test` for unit, `[TargetClass]IntegrationTest` for integration.

---

## Stop Condition

**Step 1:** Stop after generating the 테스트 코드 작성 계획서. Wait for explicit user approval.

**Step 2:** Stop after all tests are written and run. Report results to the user, then proceed to Phase 3.
