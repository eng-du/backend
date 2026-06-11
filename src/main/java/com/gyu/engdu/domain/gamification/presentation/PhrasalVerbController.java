package com.gyu.engdu.domain.gamification.presentation;

import com.gyu.engdu.domain.gamification.application.PhrasalVerbQueryService;
import com.gyu.engdu.domain.gamification.application.dto.response.PhrasalVerbResponse;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phrasal-verb")
public class PhrasalVerbController {

    private final PhrasalVerbQueryService phrasalVerbQueryService;

    @GetMapping
    public PhrasalVerbResponse getRandomPhrasalVerb(
            @RequestParam(required = false) List<Long> excludeIds) {
        Long maxId = phrasalVerbQueryService.getMaxId();
        long pivot = ThreadLocalRandom.current().nextLong(1, maxId + 1);

        if (excludeIds == null || excludeIds.isEmpty()) {
            return phrasalVerbQueryService.getPhrasalVerbByPivot(pivot);
        }
        return phrasalVerbQueryService.getPhrasalVerbByPivotAndExclusion(pivot, excludeIds);
    }
}
