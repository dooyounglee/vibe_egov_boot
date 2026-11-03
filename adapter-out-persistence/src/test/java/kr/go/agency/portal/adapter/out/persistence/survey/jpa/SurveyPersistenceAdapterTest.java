package kr.go.agency.portal.adapter.out.persistence.survey.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.domain.survey.Survey;
import kr.go.agency.portal.domain.survey.SurveyQuestion;

/**
 * 1:N 어댑터 ↔ DB 검증: cascade 저장, 순서 유지, orphanRemoval, 트랜잭션 밖 조회.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SurveyPersistenceAdapter.class)
class SurveyPersistenceAdapterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 1, 9, 0, 0);

    @Autowired
    SurveyPersistenceAdapter adapter;

    @Autowired
    TestEntityManager em;

    @Test
    void 설문을_저장하면_문항도_함께_저장된다() {
        Survey saved = adapter.save(surveyWith("첫 번째", "두 번째", "세 번째"));
        flushAndClear();

        Survey found = adapter.findById(saved.getId()).orElseThrow();

        assertThat(found.getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("첫 번째", "두 번째", "세 번째");
        assertThat(found.getQuestions())
                .extracting(SurveyQuestion::getId)
                .doesNotContainNull();
    }

    @Test
    void 문항을_삭제하고_추가하면_DB에도_반영된다() {
        Survey survey = adapter.save(surveyWith("첫 번째", "두 번째"));
        flushAndClear();
        Long keptQuestionId = survey.getQuestions().get(1).getId();

        survey.removeQuestion(0);
        survey.addQuestion("새 문항");
        adapter.save(survey);
        flushAndClear();

        Survey found = adapter.findById(survey.getId()).orElseThrow();
        assertThat(found.getQuestions())
                .extracting(SurveyQuestion::getContent)
                .containsExactly("두 번째", "새 문항");
        assertThat(found.getQuestions().get(0).getId()).isEqualTo(keptQuestionId);
        assertThat(questionCount(survey.getId())).isEqualTo(2L); // 빠진 문항은 orphanRemoval 로 삭제
    }

    @Test
    void 문항이_없는_설문도_조회된다() {
        Long id = adapter.save(Survey.create("빈 설문", NOW)).getId();
        flushAndClear();

        assertThat(adapter.findById(id).orElseThrow().getQuestions()).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 트랜잭션_밖에서도_문항까지_조회된다() {
        Long id = adapter.save(surveyWith("첫 번째", "두 번째")).getId();

        // 문항을 EntityGraph 로 함께 읽지 않으면 여기서 LazyInitializationException 이 난다
        Survey found = adapter.findById(id).orElseThrow();

        assertThat(found.getQuestions()).hasSize(2);
    }

    @Test
    void 없는_식별자는_빈_결과를_돌려준다() {
        assertThat(adapter.findById(999_999L)).isEmpty();
    }

    private static Survey surveyWith(String... questions) {
        Survey survey = Survey.create("만족도 조사", NOW);
        for (String question : questions) {
            survey.addQuestion(question);
        }
        return survey;
    }

    private long questionCount(Long surveyId) {
        return em.getEntityManager()
                .createQuery("select count(q) from SurveyQuestionJpaEntity q where q.survey.id = :id", Long.class)
                .setParameter("id", surveyId)
                .getSingleResult();
    }

    private void flushAndClear() {
        em.flush();
        em.clear(); // 1차 캐시가 아닌 DB에서 읽도록 비운다
    }
}
