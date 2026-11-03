package kr.go.agency.portal.application.survey.port.out;

import kr.go.agency.portal.domain.survey.Survey;

public interface SaveSurveyPort {

    /**
     * 설문과 문항을 함께 저장한다. 목록에서 빠진 문항은 삭제된다.
     *
     * @return 설문·문항에 식별자가 부여된 설문
     */
    Survey save(Survey survey);
}
