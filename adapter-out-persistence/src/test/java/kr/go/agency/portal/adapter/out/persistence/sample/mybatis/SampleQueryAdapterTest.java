package kr.go.agency.portal.adapter.out.persistence.sample.mybatis;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import kr.go.agency.portal.adapter.out.persistence.config.MyBatisConfig;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;

/**
 * MyBatis 어댑터 ↔ DB 검증. Mapper는 SELECT만 두는 규칙이므로 데이터는 @Sql 로 넣고, 테스트가 끝나면 롤백된다.
 * MyBatisConfig 는 @EgovMapper 스캔을 위해 가져온다 (@MybatisTest 기본 스캔은 @Mapper 만 대상).
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ MyBatisConfig.class, SampleQueryAdapter.class })
@Sql("/sql/sample-search.sql")
class SampleQueryAdapterTest {

    @Autowired
    SampleQueryAdapter adapter;

    @Test
    void 최신순으로_페이지를_나눈다() {
        PageResult<SampleSummary> result = adapter.search(new SampleSearchCondition(null, 2, 5));

        assertThat(result.totalCount()).isEqualTo(12);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.items()).extracting(SampleSummary::id).containsExactly(7L, 6L, 5L, 4L, 3L);
    }

    @Test
    void 마지막_페이지는_남은_건수만_돌려준다() {
        PageResult<SampleSummary> result = adapter.search(new SampleSearchCondition(null, 3, 5));

        assertThat(result.items()).extracting(SampleSummary::id).containsExactly(2L, 1L);
    }

    @Test
    void 키워드로_제목을_검색한다() {
        PageResult<SampleSummary> result = adapter.search(new SampleSearchCondition("공지", 1, 10));

        assertThat(result.totalCount()).isEqualTo(6);
        assertThat(result.items()).extracting(SampleSummary::id).containsExactly(11L, 9L, 7L, 5L, 3L, 1L);
    }

    @Test
    void 검색_결과가_없으면_빈_페이지를_돌려준다() {
        PageResult<SampleSummary> result = adapter.search(new SampleSearchCondition("없는제목", 1, 10));

        assertThat(result.totalCount()).isZero();
        assertThat(result.items()).isEmpty();
    }

    @Test
    void 조회_결과가_조회모델로_매핑된다() {
        SampleSummary first = adapter.search(new SampleSearchCondition("공지 11", 1, 10)).items().get(0);

        assertThat(first).isEqualTo(new SampleSummary(11L, "공지 11", LocalDateTime.of(2026, 11, 1, 9, 11, 0)));
    }
}
