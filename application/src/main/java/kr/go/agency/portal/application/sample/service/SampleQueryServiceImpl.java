package kr.go.agency.portal.application.sample.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.sample.port.in.GetSampleService;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService;
import kr.go.agency.portal.application.sample.port.out.LoadSamplePort;
import kr.go.agency.portal.application.sample.port.out.SearchSamplePort;

@Service
@Transactional(readOnly = true)
public class SampleQueryServiceImpl implements GetSampleService, SearchSampleService {

    private final LoadSamplePort loadSamplePort;
    private final SearchSamplePort searchSamplePort;

    public SampleQueryServiceImpl(LoadSamplePort loadSamplePort, SearchSamplePort searchSamplePort) {
        this.loadSamplePort = loadSamplePort;
        this.searchSamplePort = searchSamplePort;
    }

    @Override
    public SampleResult get(Long id) {
        return loadSamplePort.findById(id)
                .map(SampleResult::from)
                .orElseThrow(() -> new NotFoundException("샘플을 찾을 수 없습니다. id=" + id));
    }

    @Override
    public PageResult<SampleSummary> search(SampleSearchCondition condition) {
        return searchSamplePort.search(condition);
    }
}
