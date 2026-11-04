package kr.go.agency.portal.domain.survey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import kr.go.agency.portal.domain.common.DomainException;

class SurveyTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 1, 9, 0);

    @Test
    void 문항은_추가한_순서대로_유지된다() {
        Survey survey = Survey.create("만족도 조사", NOW);
        survey.addQuestion("  첫 번째  ");
        survey.addQuestion("두 번째");

        assertThat(survey.getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("첫 번째", "두 번째");
    }

    @Test
    void 문항ID로_문항을_삭제할_수_있다() {
        Survey survey = Survey.restore(1L, "만족도 조사",
                List.of(SurveyQuestion.restore(10L, "첫 번째"), SurveyQuestion.restore(11L, "두 번째")), NOW);

        survey.removeQuestion(10L);

        assertThat(survey.getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("두 번째");
    }

    @Test
    void 없는_문항은_삭제할_수_없다() {
        Survey survey = Survey.restore(1L, "만족도 조사", List.of(SurveyQuestion.restore(10L, "첫 번째")), NOW);

        assertThatThrownBy(() -> survey.removeQuestion(99L))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 문항은_최대_개수를_넘을_수_없다() {
        Survey survey = Survey.create("만족도 조사", NOW);
        for (int i = 0; i < Survey.QUESTIONS_MAX_SIZE; i++) {
            survey.addQuestion("문항" + i);
        }

        assertThatThrownBy(() -> survey.addQuestion("초과"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 문항_목록은_밖에서_수정할_수_없다() {
        Survey survey = Survey.create("만족도 조사", NOW);

        assertThatThrownBy(() -> survey.getQuestions().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void 빈_문항은_추가할_수_없다() {
        Survey survey = Survey.create("만족도 조사", NOW);

        assertThatThrownBy(() -> survey.addQuestion(" "))
                .isInstanceOf(DomainException.class);
    }
}
