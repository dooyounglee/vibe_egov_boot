package kr.go.agency.portal.application.survey.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService;
import kr.go.agency.portal.application.survey.port.out.LoadSurveyPort;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyQueryServiceImpl implements GetSurveyService {

    private final LoadSurveyPort loadSurveyPort;

    @Override
    public SurveyResult get(Long id) {
        return loadSurveyPort.findById(id)
                .map(SurveyResult::from)
                .orElseThrow(() -> new NotFoundException("설문을 찾을 수 없습니다. id=" + id));
    }
}
