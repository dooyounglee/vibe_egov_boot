package kr.go.agency.portal.application.sample.port.out;

import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;

/**
 * 조회 전용 포트. 구현체는 MyBatis 어댑터이다.
 */
public interface SearchSamplePort {

    PageResult<SampleSummary> search(SampleSearchCondition condition);
}
