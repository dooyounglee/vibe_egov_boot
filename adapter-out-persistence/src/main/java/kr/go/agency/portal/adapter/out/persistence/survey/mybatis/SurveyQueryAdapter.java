package kr.go.agency.portal.adapter.out.persistence.survey.mybatis;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;
import kr.go.agency.portal.application.survey.port.out.SearchSurveyPort;
import lombok.RequiredArgsConstructor;

/**
 * 설문 목록·검색 어댑터 (MyBatis).
 */
@Component
@RequiredArgsConstructor
class SurveyQueryAdapter implements SearchSurveyPort {

    private final SurveyQueryMapper mapper;

    @Override
    public PageResult<SurveySummary> search(SurveySearchCondition condition) {
        long totalCount = mapper.selectSurveyCount(condition);
        List<SurveySummary> items = totalCount == 0 ? List.of() : mapper.selectSurveyList(condition);
        return new PageResult<>(items, totalCount, condition.page(), condition.size());
    }
}
