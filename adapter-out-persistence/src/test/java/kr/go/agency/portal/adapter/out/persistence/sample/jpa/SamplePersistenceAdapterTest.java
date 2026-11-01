package kr.go.agency.portal.adapter.out.persistence.sample.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import kr.go.agency.portal.domain.sample.Sample;

/**
 * JPA 어댑터 ↔ DB 검증. 스키마는 운영 DDL로 만들고 ddl-auto=validate 이므로
 * 컨텍스트가 뜨는 것 자체가 엔티티와 DDL이 일치한다는 의미이다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SamplePersistenceAdapter.class)
class SamplePersistenceAdapterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 1, 9, 0, 0);

    @Autowired
    SamplePersistenceAdapter adapter;

    @Autowired
    TestEntityManager em;

    @Test
    void 저장하면_시퀀스로_식별자가_부여된다() {
        Sample first = adapter.save(Sample.create("제목1", "내용", NOW));
        Sample second = adapter.save(Sample.create("제목2", "내용", NOW));

        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isGreaterThan(first.getId());
    }

    @Test
    void 저장한_샘플을_식별자로_다시_읽을_수_있다() {
        Long id = adapter.save(Sample.create("  공지  ", "본문", NOW)).getId();
        em.flush();
        em.clear(); // 1차 캐시가 아닌 DB에서 읽도록 비운다

        Sample found = adapter.findById(id).orElseThrow();

        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getTitle()).isEqualTo("공지");
        assertThat(found.getContent()).isEqualTo("본문");
        assertThat(found.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void 최대_길이의_제목과_내용을_저장할_수_있다() {
        String title = "가".repeat(Sample.TITLE_MAX_LENGTH);
        String content = "나".repeat(Sample.CONTENT_MAX_LENGTH);

        Long id = adapter.save(Sample.create(title, content, NOW)).getId();
        em.flush();
        em.clear();

        Sample found = adapter.findById(id).orElseThrow();
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getContent()).isEqualTo(content);
    }

    @Test
    void 없는_식별자는_빈_결과를_돌려준다() {
        assertThat(adapter.findById(999_999L)).isEmpty();
    }
}
