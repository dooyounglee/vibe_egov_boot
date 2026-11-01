package kr.go.agency.portal.adapter.out.persistence.config;

import org.egovframe.rte.psl.dataaccess.mapper.EgovMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 등록. 표준프레임워크 규약에 따라 @EgovMapper 가 붙은 인터페이스만 스캔한다.
 * SqlSessionFactory 와 트랜잭션은 Spring Boot 자동 설정을 사용하며,
 * JPA의 JpaTransactionManager 가 같은 커넥션을 공유하므로 한 트랜잭션 안에서 함께 동작한다.
 */
@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = "kr.go.agency.portal.adapter.out.persistence", annotationClass = EgovMapper.class)
public class MyBatisConfig {
}
