package kr.go.agency.portal.domain.sample;

import java.time.LocalDateTime;
import java.util.Objects;

import kr.go.agency.portal.domain.common.DomainException;

/**
 * 샘플 도메인 모델. 헥사고날 구조의 흐름을 보여주기 위한 예시이다.
 */
public final class Sample {

    public static final int TITLE_MAX_LENGTH = 100;
    public static final int CONTENT_MAX_LENGTH = 4000;

    private final Long id;
    private String title;
    private String content;
    private final LocalDateTime createdAt;

    private Sample(Long id, String title, String content, LocalDateTime createdAt) {
        validate(title, content);
        this.id = id;
        this.title = title.strip();
        this.content = content;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /** 신규 생성 (아직 식별자가 없음) */
    public static Sample create(String title, String content, LocalDateTime now) {
        return new Sample(null, title, content, now);
    }

    /** 저장소에서 읽어온 상태로 복원 */
    public static Sample restore(Long id, String title, String content, LocalDateTime createdAt) {
        return new Sample(Objects.requireNonNull(id, "id"), title, content, createdAt);
    }

    public void change(String title, String content) {
        validate(title, content);
        this.title = title.strip();
        this.content = content;
    }

    private static void validate(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new DomainException("제목은 필수입니다.");
        }
        if (title.strip().length() > TITLE_MAX_LENGTH) {
            throw new DomainException("제목은 " + TITLE_MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new DomainException("내용은 " + CONTENT_MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
