package kr.go.agency.portal.domain.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kr.go.agency.portal.domain.common.DomainException;

class SampleTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 1, 9, 0);

    @Test
    void 신규_생성시_제목_앞뒤_공백을_제거한다() {
        Sample sample = Sample.create("  공지사항  ", "내용", NOW);

        assertThat(sample.getId()).isNull();
        assertThat(sample.getTitle()).isEqualTo("공지사항");
        assertThat(sample.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void 제목이_비어있으면_생성할_수_없다() {
        assertThatThrownBy(() -> Sample.create(" ", "내용", NOW))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 제목이_최대길이를_넘으면_생성할_수_없다() {
        String longTitle = "가".repeat(Sample.TITLE_MAX_LENGTH + 1);

        assertThatThrownBy(() -> Sample.create(longTitle, "내용", NOW))
                .isInstanceOf(DomainException.class);
    }
}
