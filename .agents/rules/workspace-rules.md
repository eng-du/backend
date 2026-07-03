---
trigger: always_on
---

# 코드 작성 규칙 (Coding Policy)
- 절대 클래스나 Enum의 전체 패키지 경로(Fully Qualified Name)를 코드 라인 내에 직접 하드코딩해서 사용하지 마라.
- Exception, Enum, 객체 등을 참조할 때는 반드시 파일 상단에 `import` 문을 선언하고, 코드 내부에서는 클래스명만 깔끔하게 사용해라.
  - [잘못된 예] `.provider(com.gyu.engdu.domain.auth.domain.OAuthProvider.GOOGLE)`
  - [올바른 예] `import com.gyu.engdu.domain.auth.domain.OAuthProvider;` 선언 후 `.provider(OAuthProvider.GOOGLE)` 사용

# github PR 가이드
- 깃허브에 PR을 작성할 때는 /Users/younggyu/Desktop/eng-du-be/.github/pull_request_template.md 파일을 참고해라.
- PR 제목은 '[FEAT or CHORE or REFACTOR] 이슈명 or 내용 요약'으로 구성한다. 
- 필요하다면 파일이름을 사용해라. 파일 이름에서 파일 경로는 제거한다.
- 과정보다 결과를 중심으로 PR을 작성해라.
- PR은 현재 브랜치에서 dev 브랜치로 머지하기 위한 PR이다.
- PR은 young999999999/eng-du-be 가 아닌 https://github.com/eng-du/backend에 올려야한다.
- PR의 내용을 작성하고 초안을 실행 계획으로 보여라. 나에게 리뷰를 받고 올려야한다.

# 브랜치 네이밍 규칙
- 브랜치 이름은 `[타입]/#[이슈번호]-[기능-요약]` 형식으로 작성한다.
- 타입은 소문자로 작성하며, 기능 요약은 케밥 케이스(kebab-case)를 사용한다.
- 예시: `feat/#139-run-and-learn-submit`, `fix/#136-exception-message`, `chore/#143-pr-template-improvement`

# 커밋 타입 분류
- feat: 새로운 기능 구현 (도메인 로직 추가, 새로운 API 개발, 엔티티 설계 등)
- fix: 버그 수정 및 예외 처리 (오류 해결, 환경 설정 오류 복구 등)
- refactor: 기존 기능의 변경 없이 내부 구조나 로직을 개선 (응답 구조 변경, 변수명/메시지명 수정, 구조 리팩토링 등)
- chore: 빌드, 환경 설정, 패키지 매니저 관리, DDL 수정 등 비즈니스 로직과 직접적 관련이 없는 작업 (의존성 추가, CI/CD yml 수정, 메트릭 설정 등)
- test: 테스트 코드 추가 및 수정 (단위 테스트, 통합 테스트 서포트 객체 추가 등)
- style: 코드 형식(포맷팅) 수정, 불필요한 코드 및 주석 제거, 사용하지 않는 메서드 삭제 등


# 커밋 주의사항
- 환경 파일, 설정 파일로 보이면 너가 임의로 커밋하지마라.
- 코드 리뷰 피드백을 반영한 커밋을 생성할 때는 `[타입]: [리뷰반영] 커밋 내용` 형식으로 작성하여 가독성을 높인다.
  - 예시: `chore: [리뷰반영] 코드 리뷰 피드백 반영 (헤더 앞 빈 줄 추가)`

