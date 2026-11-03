package kr.go.agency.portal.application.survey.port.out;

import java.util.Optional;

import kr.go.agency.portal.domain.survey.Survey;

public interface LoadSurveyPort {

    /** 문항까지 모두 채워진 설문을 돌려준다. */
    Optional<Survey> findById(Long id);
}
