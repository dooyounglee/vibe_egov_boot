package kr.go.agency.portal.adapter.out.persistence.survey.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import kr.go.agency.portal.domain.survey.SurveyQuestion;

/**
 * TB_SURVEY_QUESTION 매핑용 JPA 엔티티. FK(SURVEY_ID)를 가지므로 연관관계의 주인이다.
 * ManyToOne 의 기본 fetch 는 EAGER 이므로 LAZY 로 바꿔 둔다.
 */
@Entity
@Table(name = "TB_SURVEY_QUESTION")
public class SurveyQuestionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SURVEY_QUESTION_GEN")
    @SequenceGenerator(name = "SEQ_SURVEY_QUESTION_GEN", sequenceName = "SEQ_SURVEY_QUESTION", allocationSize = 1)
    @Column(name = "QUESTION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_ID", nullable = false)
    private SurveyJpaEntity survey;

    @Column(name = "CONTENT", nullable = false, length = SurveyQuestion.CONTENT_MAX_LENGTH)
    private String content;

    @Column(name = "SORT_ORDER", nullable = false)
    private int sortOrder;

    protected SurveyQuestionJpaEntity() {
    }

    static SurveyQuestionJpaEntity from(SurveyQuestion question, int sortOrder) {
        SurveyQuestionJpaEntity entity = new SurveyQuestionJpaEntity();
        entity.id = question.getId();
        entity.content = question.getContent();
        entity.sortOrder = sortOrder;
        return entity;
    }

    void assignTo(SurveyJpaEntity survey) {
        this.survey = survey;
    }

    SurveyQuestion toDomain() {
        return SurveyQuestion.restore(id, content);
    }
}
