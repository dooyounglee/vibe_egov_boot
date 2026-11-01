package kr.go.agency.portal.adapter.out.persistence.sample.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface SampleJpaRepository extends JpaRepository<SampleJpaEntity, Long> {
}
