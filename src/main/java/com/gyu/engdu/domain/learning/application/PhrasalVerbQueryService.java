package com.gyu.engdu.domain.learning.application;

import com.gyu.engdu.domain.learning.application.dto.PhrasalVerbResponse;
import com.gyu.engdu.domain.learning.domain.PhrasalVerb;
import com.gyu.engdu.domain.learning.domain.PhrasalVerbRepository;
import com.gyu.engdu.domain.learning.exception.PhrasalVerbNotFoundException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhrasalVerbQueryService {

    private final PhrasalVerbRepository phrasalVerbRepository;

    public Long getMaxId() {
        Long maxId = phrasalVerbRepository.findMaxId();
        if (maxId == null) {
            throw new PhrasalVerbNotFoundException();
        }
        return maxId;
    }

    public PhrasalVerbResponse getPhrasalVerbByPivot(long pivot) {
        PhrasalVerb phrasalVerb = phrasalVerbRepository.findFirstByIdGreaterThanEqual(pivot);

        return PhrasalVerbResponse.fromEntity(phrasalVerb);
    }

    public PhrasalVerbResponse getPhrasalVerbByPivotAndExclusion(long pivot, Collection<Long> excludedIds) {
        PhrasalVerb phrasalVerb = phrasalVerbRepository.findFirstByIdGreaterThanEqualAndIdNotIn(pivot, excludedIds);

        return PhrasalVerbResponse.fromEntity(phrasalVerb);
    }
}
