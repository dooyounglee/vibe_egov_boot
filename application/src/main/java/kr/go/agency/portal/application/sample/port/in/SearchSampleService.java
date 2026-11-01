package kr.go.agency.portal.application.sample.port.in;

import java.time.LocalDateTime;

import kr.go.agency.portal.application.common.PageResult;

/**
 * 목록·검색 조회. 화면용 조회 모델(SampleSummary)을 바로 반환한다.
 */
public interface SearchSampleService {

    PageResult<SampleSummary> search(SampleSearchCondition condition);

    record SampleSearchCondition(String keyword, int page, int size) {

        public static final int MAX_SIZE = 100;

        public SampleSearchCondition {
            page = Math.max(page, 1);
            size = size < 1 ? 10 : Math.min(size, MAX_SIZE);
            keyword = (keyword == null || keyword.isBlank()) ? null : keyword.strip();
        }

        public int offset() {
            return (page - 1) * size;
        }
    }

    record SampleSummary(Long id, String title, LocalDateTime createdAt) {
    }
}
