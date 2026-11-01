package kr.go.agency.portal.application.sample.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.sample.port.in.RegisterSampleService;
import kr.go.agency.portal.application.sample.port.out.SaveSamplePort;
import kr.go.agency.portal.domain.sample.Sample;

@Service
@Transactional
public class SampleCommandServiceImpl implements RegisterSampleService {

    private final SaveSamplePort saveSamplePort;
    private final Clock clock;

    public SampleCommandServiceImpl(SaveSamplePort saveSamplePort, Clock clock) {
        this.saveSamplePort = saveSamplePort;
        this.clock = clock;
    }

    @Override
    public Long register(RegisterSampleCommand command) {
        Sample sample = Sample.create(command.title(), command.content(), LocalDateTime.now(clock));
        return saveSamplePort.save(sample).getId();
    }
}
