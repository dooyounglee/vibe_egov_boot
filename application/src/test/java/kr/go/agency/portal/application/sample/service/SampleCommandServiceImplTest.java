package kr.go.agency.portal.application.sample.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kr.go.agency.portal.application.sample.port.in.RegisterSampleService.RegisterSampleCommand;
import kr.go.agency.portal.application.sample.port.out.SaveSamplePort;
import kr.go.agency.portal.domain.sample.Sample;

class SampleCommandServiceImplTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-01T00:00:00Z"), SEOUL);

    /** 포트를 메모리 구현으로 대체해 어댑터 없이 유스케이스만 검증한다. */
    static class FakeSaveSamplePort implements SaveSamplePort {
        final List<Sample> saved = new ArrayList<>();

        @Override
        public Sample save(Sample sample) {
            Sample stored = Sample.restore((long) saved.size() + 1, sample.getTitle(), sample.getContent(),
                    sample.getCreatedAt());
            saved.add(stored);
            return stored;
        }
    }

    @Test
    void 등록하면_저장포트를_통해_식별자를_돌려준다() {
        FakeSaveSamplePort port = new FakeSaveSamplePort();
        SampleCommandServiceImpl service = new SampleCommandServiceImpl(port, clock);

        Long id = service.register(new RegisterSampleCommand("제목", "내용"));

        assertThat(id).isEqualTo(1L);
        assertThat(port.saved).singleElement()
                .satisfies(s -> assertThat(s.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 11, 1, 9, 0)));
    }
}
