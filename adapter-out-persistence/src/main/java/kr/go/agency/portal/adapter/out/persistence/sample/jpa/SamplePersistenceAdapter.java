package kr.go.agency.portal.adapter.out.persistence.sample.jpa;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.go.agency.portal.application.sample.port.out.LoadSamplePort;
import kr.go.agency.portal.application.sample.port.out.SaveSamplePort;
import kr.go.agency.portal.domain.sample.Sample;
import lombok.RequiredArgsConstructor;

/**
 * 쓰기·단건 조회 어댑터 (JPA).
 */
@Component
@RequiredArgsConstructor
class SamplePersistenceAdapter implements SaveSamplePort, LoadSamplePort {

    private final SampleJpaRepository repository;

    @Override
    public Sample save(Sample sample) {
        return repository.save(SampleJpaEntity.from(sample)).toDomain();
    }

    @Override
    public Optional<Sample> findById(Long id) {
        return repository.findById(id).map(SampleJpaEntity::toDomain);
    }
}
