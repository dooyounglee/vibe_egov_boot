package kr.go.agency.portal.application.survey.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.survey.port.in.ChangeSurveyQuestionService;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService;
import kr.go.agency.portal.application.survey.port.out.LoadSurveyPort;
import kr.go.agency.portal.application.survey.port.out.SaveSurveyPort;
import kr.go.agency.portal.domain.survey.Survey;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SurveyCommandServiceImpl implements RegisterSurveyService, ChangeSurveyQuestionService {

    private final LoadSurveyPort loadSurveyPort;
    private final SaveSurveyPort saveSurveyPort;
    private final Clock clock;

    @Override
    public Long register(RegisterSurveyCommand command) {
        Survey survey = Survey.create(command.title(), LocalDateTime.now(clock));
        command.questions().forEach(survey::addQuestion);
        return saveSurveyPort.save(survey).getId();
    }

    @Override
    public void addQuestion(Long surveyId, String content) {
        Survey survey = load(surveyId);
        survey.addQuestion(content);
        saveSurveyPort.save(survey);
    }

    @Override
    public void removeQuestion(Long surveyId, Long questionId) {
        Survey survey = load(surveyId);
        survey.removeQuestion(questionId);
        saveSurveyPort.save(survey);
    }

    private Survey load(Long surveyId) {
        return loadSurveyPort.findById(surveyId)
                .orElseThrow(() -> new NotFoundException("설문을 찾을 수 없습니다. id=" + surveyId));
    }
}
