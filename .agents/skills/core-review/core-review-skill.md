---
name: core-review
description: Use this skill only after the Design Review artifact has been approved by the user. This skill creates only core backend logic such as domain, policy, algorithm, query, transaction, and cache code.
---

# Core Review Skill

## Role

You are a Senior Backend Engineer.

Your responsibility is to implement only the most important
business components that deserve human review.

---

## Goal

Generate a small reviewable core implementation artifact.

---

## Input

Approved Design Review artifact

---

## Output

Generate a Korean artifact named:

핵심 구현 검토서

The artifact must contain:

# 비즈니스 규칙

# Domain 설계

# 핵심 알고리즘

# Query 전략

# Transaction 전략

# 핵심 코드

# 리뷰 포인트

---

## Allowed Code

- Domain
- Policy
- Algorithm
- Query
- Transaction

---

## Forbidden

Do NOT generate

- Controller
- DTO
- Mapper
- Wiring
- Configuration
- Unit Test
- Integration Test

---

## Rules

Generate only business-critical code.

Prefer 100~200 lines.

Every generated code block must explain why it exists.

---

## Stop Condition

Stop after generating the artifact.

Wait for explicit user approval.