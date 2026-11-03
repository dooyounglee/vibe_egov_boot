package kr.go.agency.portal.domain.survey;

import kr.go.agency.portal.domain.common.DomainException;

/**
 * 설문 문항. Survey 애그리거트에 속하며 Survey 를 통해서만 추가·삭제한다.
 */
public final class SurveyQuestion {

    public static final int CONTENT_MAX_LENGTH = 500;

    private final Long id;
    private final String content;

    private SurveyQuestion(Long id, String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException("문항 내용은 필수입니다.");
        }
        if (content.strip().length() > CONTENT_MAX_LENGTH) {
            throw new DomainException("문항 내용은 " + CONTENT_MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
        this.id = id;
        this.content = content.strip();
    }

    static SurveyQuestion create(String content) {
        return new SurveyQuestion(null, content);
    }

    /** 저장소에서 읽어온 상태로 복원 */
    public static SurveyQuestion restore(Long id, String content) {
        return new SurveyQuestion(id, content);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
