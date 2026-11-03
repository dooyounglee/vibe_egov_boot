package kr.go.agency.portal.adapter.out.persistence.survey.jpa;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.go.agency.portal.application.survey.port.out.LoadSurveyPort;
import kr.go.agency.portal.application.survey.port.out.SaveSurveyPort;
import kr.go.agency.portal.domain.survey.Survey;
import lombok.RequiredArgsConstructor;

/**
 * 설문 쓰기·단건 조회 어댑터 (JPA, 1:N 예시).
 */
@Component
@RequiredArgsConstructor
class SurveyPersistenceAdapter implements SaveSurveyPort, LoadSurveyPort {

    private final SurveyJpaRepository repository;

    /** merge 결과의 문항 컬렉션을 읽는 toDomain() 까지 한 트랜잭션 안에서 처리한다. */
    @Override
    @Transactional
    public Survey save(Survey survey) {
        return repository.save(SurveyJpaEntity.from(survey)).toDomain();
    }

    @Override
    public Optional<Survey> findById(Long id) {
        return repository.findWithQuestionsById(id).map(SurveyJpaEntity::toDomain);
    }
}
