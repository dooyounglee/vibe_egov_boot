package kr.go.agency.portal.adapter.out.persistence.sample.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import kr.go.agency.portal.domain.sample.Sample;

/**
 * TB_SAMPLE 매핑용 JPA 엔티티. 도메인 모델(Sample)과 분리해 어댑터 안에서만 사용한다.
 * 테이블 정의는 db/ddl/001_create_tb_sample.sql 을 기준으로 한다.
 */
@Entity
@Table(name = "TB_SAMPLE")
public class SampleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SAMPLE_GEN")
    @SequenceGenerator(name = "SEQ_SAMPLE_GEN", sequenceName = "SEQ_SAMPLE", allocationSize = 1)
    @Column(name = "SAMPLE_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false, length = Sample.TITLE_MAX_LENGTH)
    private String title;

    @Column(name = "CONTENT", length = Sample.CONTENT_MAX_LENGTH)
    private String content;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected SampleJpaEntity() {
    }

    static SampleJpaEntity from(Sample sample) {
        SampleJpaEntity entity = new SampleJpaEntity();
        entity.id = sample.getId();
        entity.title = sample.getTitle();
        entity.content = sample.getContent();
        entity.createdAt = sample.getCreatedAt();
        return entity;
    }

    Sample toDomain() {
        return Sample.restore(id, title, content, createdAt);
    }
}
