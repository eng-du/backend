package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CompletePromptGenerator implements PromptGenerator {

    @Override
    public PartType getPartType() {
        return PartType.COMPLETE;
    }

    @Override
    public Map<String, Object> generatePrompt(GenerateEngduRequest request) {
        Map<String, Object> promptMap = new HashMap<>();
        promptMap.put("instructions", getCompletePrompt());
        promptMap.put("input", """
            사용자 입력:
             - topic: %s
             - level: %s
             - previousContent: %s
            """.formatted(request.topic(), request.level(), request.previousArticleContent()));
        return promptMap;
    }

    private String getCompletePrompt() {
        return """
             ### 글 구조 요구사항

          1. 기사(article)를 작성해라.
             - 정보 전달 글(기사)을 1개 만들어라.
             - 난이도(level)에 맞는 어휘와 문장을 사용해라.
             - 본문은 영어 word count로 150words로 작성해라 (±10%).
             - 본문의 내용은 이전 내용(previousContent)과 연관이 있어야한다. 이전 글에서 이미 설명된 사건이나 문제를 다시 설명하면 안된다. 내용이 이어지지만 새로운 스토리로 구성되어야한다.
             - 이야기는 동일한 주제를 유지하지만 반복적인 설명을 피하고 새로운 관점이나 정보를 포함해야 한다.
             - 이전 이야기와 연결되지만 새로운 상황이나 인물이 등장할 수 있다.
             - 등장 인물은 이야기를 발전시키는 역할이어야 한다.
             - 사용자가 독해하기 쉽게 문장을 청크 단위로 나눠야한다. 문장 하나가 청크가 되는 것이 아닌 문장을 이루고있는 단어들을 나누어 청크로 만드는 것에 주의해라. 예를들어, 'Social media platformsemerged as powerful toolsfor connecting individualsacross geographical boundaries.' 문장이 있을 때 'Social media platform', 'emerged as powerful tools', 'for connecting individuals', 'across geographical boundaries.' 이렇게 4개의 청크로 나눌 수 있다.
             - 본문의 해석본도 적는다. 해석본은 한국어로 청크 단위로 작성해야한다. 말투는 서술형 평서문으로 작성해라.

          2. 질문(questions)을 만든다.
             - 질문은 총 2개를 만든다. 질문은 본문의 내용과 관련되어있다.
             - 문제 유형은 독해(내용 일치/불일치), 문법, 어휘(유의어/반의어) 등으로 다양하게 구성한다.
             - 질문의 선택지(choices)는 반드시 4개여야한다.
             - 질문의 문제 유형을 COMPREHENSION, VOCA, GRAMMAR 중 하나를 선택한다.
             - 해설은 본문의 내용을 기반으로해라. 정답:, 오답:과 같이 불필요한 말들은 제외한다.
             - 각 선택지의 해설(explanation)에는 특정 번호나 순서(예: '1번은...', '첫 번째 보기는...')를 지칭하는 표현을 절대 포함하지 말고, 오직 해당 선택지 내용 자체에 대한 해설만 작성해라.

          3. 전체 결과는 다음과 같은 JSON 포맷을 따른다:
             응답에는 불필요한 것을 제외하고 순수 JSON으로 준다.

        ```json
        {
          "article": {
              "chunks": [
               ["영어 청크1", "한국어 청크1"],
               ["영어 청크2", "한국어 청크2"],
              ]
           },
          "questions": [
                 {
                   "type": "COMPREHENSION or VOCA or GRAMMAR",
                   "content": "...",
                   "answer": <1~4>,
                   "choices": [
                     {
                       "seq": 1,
                       "content": "보기1 내용",
                       "explanation": "보기1 한국어 해설"
                     },
                     {
                       "seq": 2,
                       "content": "보기2 내용",
                       "explanation": "보기2 한국어 해설"
                     },
                     {
                       "seq": 3,
                       "content": "보기3 내용",
                       "explanation": "보기3 한국어 해설"
                     },
                     {
                       "seq": 4,
                       "content": "보기4 내용",
                       "explanation": "보기4 한국어 해설"
                     }
                   ]
                 },
                 { ... }
               ]
             }
             ```

           ### 사고(thinking) 지침
           - 입력된 주제와 난이도를 확인하라.
           - 글은 입력받은 주제와 연관있어야 한다. 문자 그대로의 주제를 설명하는 것이 아닌 주제를 포함하는 내용을 써라.
           - 각 본문 작성 후, 해당 내용을 확인하며 퀴즈를 생성해라.
           - 최종 출력은 **본문과 퀴즈만** 보여주고, 중간 사고 과정은 노출하지 않는다.

           위 입력을 바탕으로, 최종 결과(본문1, 질문1~2)를 작성해라.
             """;
    }
}
