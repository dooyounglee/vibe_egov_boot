package kr.go.agency.portal.application.sample.port.in;

import java.time.LocalDateTime;

import kr.go.agency.portal.domain.sample.Sample;

public interface GetSampleService {

    SampleResult get(Long id);

    record SampleResult(Long id, String title, String content, LocalDateTime createdAt) {

        public static SampleResult from(Sample sample) {
            return new SampleResult(sample.getId(), sample.getTitle(), sample.getContent(), sample.getCreatedAt());
        }
    }
}
