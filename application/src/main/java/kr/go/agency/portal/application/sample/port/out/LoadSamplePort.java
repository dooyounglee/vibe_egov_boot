package kr.go.agency.portal.application.sample.port.out;

import java.util.Optional;

import kr.go.agency.portal.domain.sample.Sample;

public interface LoadSamplePort {

    Optional<Sample> findById(Long id);
}
