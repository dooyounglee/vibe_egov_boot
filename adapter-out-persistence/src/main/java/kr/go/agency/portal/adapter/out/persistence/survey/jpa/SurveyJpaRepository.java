package kr.go.agency.portal.adapter.out.persistence.survey.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

interface SurveyJpaRepository extends JpaRepository<SurveyJpaEntity, Long> {

    /**
     * 문항을 한 번의 조인 쿼리로 함께 읽는다. 기본 findById 는 문항이 LAZY 라서
     * 트랜잭션 밖에서 toDomain() 을 호출하면 LazyInitializationException 이 난다.
     */
    @EntityGraph(attributePaths = "questions")
    Optional<SurveyJpaEntity> findWithQuestionsById(Long id);
}
