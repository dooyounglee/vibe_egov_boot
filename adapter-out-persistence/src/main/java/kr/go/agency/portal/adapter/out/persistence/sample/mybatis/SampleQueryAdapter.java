package kr.go.agency.portal.adapter.out.persistence.sample.mybatis;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;
import kr.go.agency.portal.application.sample.port.out.SearchSamplePort;

/**
 * 목록·검색 어댑터 (MyBatis).
 */
@Component
class SampleQueryAdapter implements SearchSamplePort {

    private final SampleQueryMapper mapper;

    SampleQueryAdapter(SampleQueryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PageResult<SampleSummary> search(SampleSearchCondition condition) {
        long totalCount = mapper.selectSampleCount(condition);
        List<SampleSummary> items = totalCount == 0 ? List.of() : mapper.selectSampleList(condition);
        return new PageResult<>(items, totalCount, condition.page(), condition.size());
    }
}
