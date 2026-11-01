package kr.go.agency.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 애플리케이션 진입점. 최상위 패키지에 두어 domain, application, adapter의 모든 빈과 JPA 엔티티를 스캔한다.
 */
@SpringBootApplication
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }
}
