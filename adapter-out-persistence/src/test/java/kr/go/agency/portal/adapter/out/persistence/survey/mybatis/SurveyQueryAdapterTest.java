package kr.go.agency.portal.adapter.out.persistence.survey.mybatis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import kr.go.agency.portal.adapter.out.persistence.config.MyBatisConfig;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;

/**
 * MyBatis 설문 목록 어댑터 ↔ DB 검증. 데이터는 @Sql 로 넣고, 테스트가 끝나면 롤백된다.
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ MyBatisConfig.class, SurveyQueryAdapter.class })
@Sql("/sql/survey-search.sql")
class SurveyQueryAdapterTest {

    @Autowired
    SurveyQueryAdapter adapter;

    @Test
    void 설문마다_문항_수를_함께_조회한다() {
        PageResult<SurveySummary> result = adapter.search(new SurveySearchCondition(null, 1, 10));

        assertThat(result.totalCount()).isEqualTo(4);
        assertThat(result.items())
                .extracting(SurveySummary::id, SurveySummary::questionCount)
                .containsExactly(tuple(4L, 2L), tuple(3L, 1L), tuple(2L, 0L), tuple(1L, 3L));
    }

    @Test
    void 문항_수와_관계없이_설문_단위로_페이지를_나눈다() {
        PageResult<SurveySummary> result = adapter.search(new SurveySearchCondition(null, 2, 3));

        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.items()).extracting(SurveySummary::id).containsExactly(1L);
    }

    @Test
    void 키워드로_제목을_검색한다() {
        PageResult<SurveySummary> result = adapter.search(new SurveySearchCondition("만족도", 1, 10));

        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.items()).extracting(SurveySummary::id).containsExactly(3L, 1L);
    }

    @Test
    void 검색_결과가_없으면_빈_페이지를_돌려준다() {
        PageResult<SurveySummary> result = adapter.search(new SurveySearchCondition("없는제목", 1, 10));

        assertThat(result.totalCount()).isZero();
        assertThat(result.items()).isEmpty();
    }

    @Test
    void 조회_결과가_조회모델로_매핑된다() {
        SurveySummary first = adapter.search(new SurveySearchCondition("만족도 조사 01", 1, 10)).items().get(0);

        assertThat(first).isEqualTo(new SurveySummary(1L, "만족도 조사 01", 3L, LocalDateTime.of(2026, 11, 1, 9, 1, 0)));
    }
}
