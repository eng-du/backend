이 파일은 안티그래비티가 `eng-du` 프로젝트에서 특정 Controller의 API를 분석하여 Notion '잉듀 API 문서' 데이터베이스에 자동으로 문서화할 때 반드시 따라야 하는 지침서입니다.

## 1. 사전 분석 (Pre-analysis)
API 문서를 작성하기 전에 지정된 Controller 파일 및 관련 DTO 클래스들을 꼼꼼하게 분석해야 합니다.
- **엔드포인트 추출**: `@GetMapping`, `@PostMapping` 등의 어노테이션을 확인하여 HTTP Method와 URL 경로를 추출합니다.
- **인증 여부 확인**: 컨트롤러 메서드의 파라미터에 `@AuthenticationPrincipal` 어노테이션이 존재하는지 확인하여 '인증 필요' 또는 '인증 불필요'를 판별합니다.
- **DTO 분석**: 요청(Request) 및 응답(Response)에 사용되는 DTO 클래스들의 필드를 확인하여 실제 JSON 구조(Body, Query Params 등)를 파악합니다.

## 2. 노션 데이터베이스 정보 (Notion Database Context)
- **타겟 데이터베이스 ID**: `2e885833-5231-80df-b5bc-f132fc148100` (잉듀 API 문서)
- **카테고리 지정**: API가 속한 도메인(예: 런앤런 구동사, 유저, 어휘 등)을 판단하여 `카테고리` 속성에 지정합니다. 판단이 어려운 경우 사용자에게 질문하거나 임의로 지정 후 컨펌을 받습니다.

## 3. 노션 페이지 속성 매핑 규칙 (Property Mapping Rules)
Notion 페이지 속성(Properties)은 다음 규칙에 따라 매핑합니다.
- `Method` (select): GET, POST, PUT, DELETE, PATCH 중 하나를 지정합니다.
- `URL` (url): API의 정확한 엔드포인트 주소를 작성합니다. (예: `/api/v1/run-and-learn`)
- `기능` (title): API의 핵심 기능을 요약하여 제목으로 작성합니다. (예: 런앤런 세션 생성하기)
- `설명` (rich_text): API가 어떤 역할을 하는지 짧고 명확하게 설명합니다.
- `인증 유무` (select): '인증 필요' 또는 '인증 불필요' 중 하나를 지정합니다.
- `카테고리` (multi_select): 도메인 이름(예: '런앤런 구동사')을 지정합니다.

## 4. 본문 블록 작성 규칙 (Body Block Formatting Rules)
Notion API의 `children` 속성을 사용하여 아래와 같은 구조로 본문 블록을 구성합니다. 모든 블록은 JSON Object 형태로 엄격하게 작성해야 합니다.

1. **Heading 2 (API 기능 제목)**: 기능 요약 제목을 작성합니다.
2. **Code (API 경로)**: 언어는 `plain text`로 지정하고, `[HTTP Method] [URL 경로]` (필요시 쿼리 파라미터 포함) 형식으로 작성합니다.
3. **Heading 3 ("Request")**: "Request"라는 텍스트로 헤더를 작성합니다.
4. **Code (요청 상세)**: 
   - 헤더(Authorization), Path Variable, Query Parameter, Request Body 등을 포함합니다.
   - Request Body가 있는 경우 언어를 `json`으로, 그 외의 경우 `plain text`로 작성합니다.
5. **Heading 3 ("Response")**: "Response"라는 텍스트로 헤더를 작성합니다.
6. **Code (응답 상세)**: 
   - Response Body 구조를 JSON 형태로 작성하며 언어는 `json`으로 지정합니다.
   - 응답이 비어있는 경우 `plain text`로 `HTTP Status: 200 OK (Empty Body)`와 같이 명시합니다.

## 5. 실행 및 검증 (Execution & Verification)
- **도구 사용**: `mcp_notion-mcp-server_API-post-page` 도구를 사용하여 노션 페이지를 생성합니다.
- **병렬 실행**: 문서화할 API가 여러 개일 경우, 도구를 병렬(동시)로 호출하여 작업 속도를 최적화합니다.
- **검증 및 보고**: 도구 실행 완료 후 정상적으로 생성되었는지 반환 결과를 확인하고, 사용자에게 완료 보고를 진행합니다.


## 6. 중복 문서 방지 규칙

- Notion 페이지 생성 전에 반드시 기존 문서 조회

- `Method + 정규화된 URL` 조합을 API 고유 식별자로 사용

- 기존 문서가 있으면 새 페이지 생성 금지

- 수정 도구가 있으면 기존 페이지 업데이트

- 수정 도구가 없으면 스킵하고 사용자에게 보고

- Query Parameter 값만 다른 API를 별도 페이지로 중복 생성 금지