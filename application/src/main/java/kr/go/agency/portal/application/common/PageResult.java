package kr.go.agency.portal.application.common;

import java.util.List;

/**
 * 목록 조회 결과 (페이지 단위).
 *
 * @param page 1부터 시작하는 페이지 번호
 */
public record PageResult<T>(List<T> items, long totalCount, int page, int size) {

    public PageResult {
        items = List.copyOf(items);
    }

    public int totalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) totalCount / size);
    }
}
