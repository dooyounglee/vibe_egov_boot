package kr.go.agency.portal.application.survey.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService;
import kr.go.agency.portal.application.survey.port.out.LoadSurveyPort;
import kr.go.agency.portal.application.survey.port.out.SearchSurveyPort;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyQueryServiceImpl implements GetSurveyService, SearchSurveyService {

    private final LoadSurveyPort loadSurveyPort;
    private final SearchSurveyPort searchSurveyPort;

    @Override
    public SurveyResult get(Long id) {
        return loadSurveyPort.findById(id)
                .map(SurveyResult::from)
                .orElseThrow(() -> new NotFoundException("설문을 찾을 수 없습니다. id=" + id));
    }

    @Override
    public PageResult<SurveySummary> search(SurveySearchCondition condition) {
        return searchSurveyPort.search(condition);
    }
}
