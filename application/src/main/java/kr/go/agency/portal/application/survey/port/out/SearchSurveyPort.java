package kr.go.agency.portal.application.survey.port.out;

import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;

/**
 * 조회 전용 포트. 구현체는 MyBatis 어댑터이다.
 */
public interface SearchSurveyPort {

    PageResult<SurveySummary> search(SurveySearchCondition condition);
}
