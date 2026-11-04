package kr.go.agency.portal.application.survey.port.in;

import java.time.LocalDateTime;
import java.util.List;

import kr.go.agency.portal.domain.survey.Survey;

public interface GetSurveyService {

    SurveyResult get(Long id);

    record SurveyResult(Long id, String title, List<QuestionResult> questions, LocalDateTime createdAt) {

        public static SurveyResult from(Survey survey) {
            List<QuestionResult> questions = survey.getQuestions().stream()
                    .map(q -> new QuestionResult(q.getId(), q.getContent()))
                    .toList();
            return new SurveyResult(survey.getId(), survey.getTitle(), questions, survey.getCreatedAt());
        }
    }

    record QuestionResult(Long id, String content) {
    }
}
