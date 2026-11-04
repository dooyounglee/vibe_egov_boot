package kr.go.agency.portal.application.survey.port.in;

import java.util.List;

public interface RegisterSurveyService {

    /**
     * 설문과 문항을 함께 등록한다.
     *
     * @return 등록된 설문의 식별자
     */
    Long register(RegisterSurveyCommand command);

    /** @param questions 문항 내용 (목록 순서가 문항 순서) */
    record RegisterSurveyCommand(String title, List<String> questions) {

        public RegisterSurveyCommand {
            questions = questions == null ? List.of() : List.copyOf(questions);
        }
    }
}
