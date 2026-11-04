package kr.go.agency.portal.domain.survey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import kr.go.agency.portal.domain.common.DomainException;

/**
 * 설문 도메인 모델. 1:N(OneToMany) 매핑 예시용 애그리거트로, 문항(SurveyQuestion)을 소유한다.
 * 문항의 순서는 목록 순서 그대로이며, 저장 시 어댑터가 SORT_ORDER 로 기록한다.
 */
public final class Survey {

    public static final int TITLE_MAX_LENGTH = 100;
    public static final int QUESTIONS_MAX_SIZE = 20;

    private final Long id;
    private String title;
    private final List<SurveyQuestion> questions;
    private final LocalDateTime createdAt;

    private Survey(Long id, String title, List<SurveyQuestion> questions, LocalDateTime createdAt) {
        validateTitle(title);
        this.id = id;
        this.title = title.strip();
        this.questions = new ArrayList<>(questions);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /** 신규 생성 (아직 식별자가 없음) */
    public static Survey create(String title, LocalDateTime now) {
        return new Survey(null, title, List.of(), now);
    }

    /** 저장소에서 읽어온 상태로 복원 */
    public static Survey restore(Long id, String title, List<SurveyQuestion> questions, LocalDateTime createdAt) {
        return new Survey(Objects.requireNonNull(id, "id"), title, questions, createdAt);
    }

    public void addQuestion(String content) {
        if (questions.size() >= QUESTIONS_MAX_SIZE) {
            throw new DomainException("문항은 " + QUESTIONS_MAX_SIZE + "개를 넘을 수 없습니다.");
        }
        questions.add(SurveyQuestion.create(content));
    }

    public void removeQuestion(Long questionId) {
        boolean removed = questionId != null && questions.removeIf(q -> questionId.equals(q.getId()));
        if (!removed) {
            throw new DomainException("존재하지 않는 문항입니다. questionId=" + questionId);
        }
    }

    public void changeTitle(String title) {
        validateTitle(title);
        this.title = title.strip();
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DomainException("제목은 필수입니다.");
        }
        if (title.strip().length() > TITLE_MAX_LENGTH) {
            throw new DomainException("제목은 " + TITLE_MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<SurveyQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
