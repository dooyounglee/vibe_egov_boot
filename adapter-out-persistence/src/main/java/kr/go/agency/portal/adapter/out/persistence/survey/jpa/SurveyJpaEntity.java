package kr.go.agency.portal.adapter.out.persistence.survey.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import kr.go.agency.portal.domain.survey.Survey;
import kr.go.agency.portal.domain.survey.SurveyQuestion;

/**
 * TB_SURVEY 매핑용 JPA 엔티티. 1:N(OneToMany) 양방향 매핑 예시이다.
 * <ul>
 *   <li>연관관계의 주인은 FK(SURVEY_ID)를 가진 문항 쪽이고, 여기서는 mappedBy 로 읽기만 한다.</li>
 *   <li>문항은 설문과 생명주기가 같으므로 cascade=ALL, 목록에서 빠지면 삭제되도록 orphanRemoval=true.</li>
 *   <li>컬렉션은 기본값(LAZY)을 유지하고, 함께 읽을 때는 리포지토리의 @EntityGraph 를 사용한다.</li>
 * </ul>
 * 테이블 정의는 db/ddl/002_create_tb_survey.sql 을 기준으로 한다.
 */
@Entity
@Table(name = "TB_SURVEY")
public class SurveyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SURVEY_GEN")
    @SequenceGenerator(name = "SEQ_SURVEY_GEN", sequenceName = "SEQ_SURVEY", allocationSize = 1)
    @Column(name = "SURVEY_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false, length = Survey.TITLE_MAX_LENGTH)
    private String title;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<SurveyQuestionJpaEntity> questions = new ArrayList<>();

    protected SurveyJpaEntity() {
    }

    static SurveyJpaEntity from(Survey survey) {
        SurveyJpaEntity entity = new SurveyJpaEntity();
        entity.id = survey.getId();
        entity.title = survey.getTitle();
        entity.createdAt = survey.getCreatedAt();
        List<SurveyQuestion> questions = survey.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            entity.addQuestion(SurveyQuestionJpaEntity.from(questions.get(i), i + 1));
        }
        return entity;
    }

    /** 양방향 연관관계는 양쪽을 함께 설정한다. */
    private void addQuestion(SurveyQuestionJpaEntity question) {
        questions.add(question);
        question.assignTo(this);
    }

    Survey toDomain() {
        List<SurveyQuestion> domainQuestions = questions.stream()
                .map(SurveyQuestionJpaEntity::toDomain)
                .toList();
        return Survey.restore(id, title, domainQuestions, createdAt);
    }
}
