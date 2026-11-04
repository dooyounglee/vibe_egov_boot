package kr.go.agency.portal.application.survey.port.in;

/**
 * 등록된 설문의 문항 추가·삭제. 문항은 설문 애그리거트를 통해서만 변경한다.
 */
public interface ChangeSurveyQuestionService {

    void addQuestion(Long surveyId, String content);

    void removeQuestion(Long surveyId, Long questionId);
}
