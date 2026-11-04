package kr.go.agency.portal.application.survey.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService.RegisterSurveyCommand;
import kr.go.agency.portal.application.survey.port.out.LoadSurveyPort;
import kr.go.agency.portal.application.survey.port.out.SaveSurveyPort;
import kr.go.agency.portal.domain.common.DomainException;
import kr.go.agency.portal.domain.survey.Survey;
import kr.go.agency.portal.domain.survey.SurveyQuestion;

class SurveyCommandServiceImplTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-01T00:00:00Z"), SEOUL);

    private final FakeSurveyPort port = new FakeSurveyPort();
    private final SurveyCommandServiceImpl service = new SurveyCommandServiceImpl(port, port, clock);

    /** 포트를 메모리 구현으로 대체해 어댑터 없이 유스케이스만 검증한다. 저장할 때 설문·문항에 식별자를 부여한다. */
    static class FakeSurveyPort implements SaveSurveyPort, LoadSurveyPort {
        final Map<Long, Survey> store = new HashMap<>();
        final AtomicLong surveySeq = new AtomicLong();
        final AtomicLong questionSeq = new AtomicLong(100);

        @Override
        public Survey save(Survey survey) {
            Long id = survey.getId() != null ? survey.getId() : surveySeq.incrementAndGet();
            List<SurveyQuestion> questions = survey.getQuestions().stream()
                    .map(q -> SurveyQuestion.restore(
                            q.getId() != null ? q.getId() : questionSeq.incrementAndGet(), q.getContent()))
                    .toList();
            Survey stored = Survey.restore(id, survey.getTitle(), questions, survey.getCreatedAt());
            store.put(id, stored);
            return stored;
        }

        @Override
        public Optional<Survey> findById(Long id) {
            Survey survey = store.get(id);
            return Optional.ofNullable(survey == null ? null
                    : Survey.restore(survey.getId(), survey.getTitle(), survey.getQuestions(), survey.getCreatedAt()));
        }
    }

    @Test
    void 등록하면_문항과_함께_저장하고_식별자를_돌려준다() {
        Long id = service.register(new RegisterSurveyCommand("만족도 조사", List.of("첫 번째", "두 번째")));

        Survey saved = port.store.get(id);
        assertThat(saved.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 11, 1, 9, 0));
        assertThat(saved.getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("첫 번째", "두 번째");
    }

    @Test
    void 문항을_추가하면_마지막에_붙는다() {
        Long id = service.register(new RegisterSurveyCommand("만족도 조사", List.of("첫 번째")));

        service.addQuestion(id, "두 번째");

        assertThat(port.store.get(id).getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("첫 번째", "두 번째");
    }

    @Test
    void 문항ID로_문항을_삭제한다() {
        Long id = service.register(new RegisterSurveyCommand("만족도 조사", List.of("첫 번째", "두 번째")));
        Long firstQuestionId = port.store.get(id).getQuestions().get(0).getId();

        service.removeQuestion(id, firstQuestionId);

        assertThat(port.store.get(id).getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("두 번째");
    }

    @Test
    void 없는_설문은_NotFound() {
        assertThatThrownBy(() -> service.addQuestion(999L, "문항"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 없는_문항을_삭제하면_도메인_예외() {
        Long id = service.register(new RegisterSurveyCommand("만족도 조사", List.of("첫 번째")));

        assertThatThrownBy(() -> service.removeQuestion(id, 999L))
                .isInstanceOf(DomainException.class);
    }
}
