package com.gyu.engdu.domain.learning.application.dto;

import com.gyu.engdu.domain.learning.domain.PhrasalVerb;

public record PhrasalVerbResponse(
        Long id,
        String en,
        String kor,
        String exampleSentenceEn,
        String exampleSentenceKor) {
    public static PhrasalVerbResponse fromEntity(PhrasalVerb phrasalVerb) {
        return new PhrasalVerbResponse(
                phrasalVerb.getId(),
                phrasalVerb.getEn(),
                phrasalVerb.getKor(),
                phrasalVerb.getExampleSentenceEn(),
                phrasalVerb.getExampleSentenceKor());
    }
}
