package kr.go.agency.portal.application.survey.port.in;

import java.time.LocalDateTime;

import kr.go.agency.portal.application.common.PageResult;

/**
 * 설문 목록·검색 조회. 화면용 조회 모델(SurveySummary)을 바로 반환한다.
 */
public interface SearchSurveyService {

    PageResult<SurveySummary> search(SurveySearchCondition condition);

    record SurveySearchCondition(String keyword, int page, int size) {

        public static final int MAX_SIZE = 100;

        public SurveySearchCondition {
            page = Math.max(page, 1);
            size = size < 1 ? 10 : Math.min(size, MAX_SIZE);
            keyword = (keyword == null || keyword.isBlank()) ? null : keyword.strip();
        }

        public int offset() {
            return (page - 1) * size;
        }
    }

    /** @param questionCount 설문에 속한 문항 수 (1:N 집계) */
    record SurveySummary(Long id, String title, long questionCount, LocalDateTime createdAt) {
    }
}
