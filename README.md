# portal

공공기관 웹 포털 백엔드 프로젝트입니다. 헥사고날 아키텍처(Ports & Adapters)로 구성했습니다.

| 항목 | 버전 |
|---|---|
| JDK | OpenJDK 21 (LTS) |
| 전자정부 표준프레임워크 | 5.0.2 (`egovframe-boot-starter-parent`) |
| Spring Boot / Spring | 3.5.6 / 6.2.11 |
| 빌드 | Maven 3.9.11 (Maven Wrapper) |
| 영속성 | JPA(Hibernate) + MyBatis 3.5 |
| 로깅 | Log4j2 (표준프레임워크 기본) |

> eGovFrame 5.x는 JDK 17 이상을 요구합니다. 이 프로젝트는 지원 기간이 더 긴 21을 사용합니다.

## JDK 설정

프로젝트 전용 JDK 경로는 **`.mvn/jdk-home` 한 곳**에만 적혀 있습니다. 시스템 PATH와 JAVA_HOME은 바꾸지 않습니다.

| 파일 | 용도 |
|---|---|
| `mvnw`, `mvnw.cmd` | `.mvn/jdk-home`의 JDK로 Maven 실행 |
| `java.cmd`, `java` | 프로젝트 JDK의 `java` 실행 (cmd·PowerShell / Git Bash) |
| `env.cmd`, `env.ps1` | 현재 터미널 세션에 JAVA_HOME·PATH 적용 |

```powershell
.\java.cmd -version      # PowerShell / cmd
. .\env.ps1              # 현재 PowerShell 세션에 JDK 적용 (실행 정책 때문에 막히면 env.cmd 사용)
```

JDK를 바꿀 때는 `.mvn/jdk-home` 파일의 경로만 수정합니다.
현재 경로의 `21+35`는 21.0.0 GA 빌드라 보안 패치가 들어 있지 않습니다. 보안 점검 전에 최신 21.0.x 업데이트 빌드로 교체하세요.

## 빌드와 실행

```powershell
.\mvnw.cmd clean verify                       # 빌드 + 전체 테스트 (아키텍처 규칙 포함)
.\mvnw.cmd -pl bootstrap -am spring-boot:run  # local 프로파일(H2)로 실행 → http://localhost:8080
.\mvnw.cmd -Pwar package                      # 외부 WAS 배포용 war (bootstrap/target/portal.war)
```

local 실행 중 사용할 수 있는 주소:
- `POST /api/samples` 등록 → `{"title": "...", "content": "..."}`
- `GET /api/samples/{id}` 단건 조회 (JPA)
- `GET /api/samples?keyword=&page=1&size=10` 검색 (MyBatis)
- `POST /api/surveys` 설문 등록 (JPA 1:N 예시) → `{"title": "...", "questions": ["...", "..."]}`
- `GET /api/surveys/{id}` 설문 조회 (문항 포함, JPA)
- `GET /api/surveys?keyword=&page=1&size=10` 설문 검색 (문항 수 포함, MyBatis)
- `POST /api/surveys/{id}/questions` 문항 추가 → `{"content": "..."}`
- `DELETE /api/surveys/{id}/questions/{questionId}` 문항 삭제
- H2 콘솔: `/h2-console` (JDBC URL `jdbc:h2:mem:portal`)

## 모듈 구조

```
domain                   순수 Java 도메인 모델·규칙 (프레임워크 의존 금지)
application              서비스 인터페이스(port.in) / 외부 의존 인터페이스(port.out) / 서비스 구현(service, *ServiceImpl)
adapter-in-web           REST Controller, 요청·응답 DTO, 공통 예외 처리
adapter-out-persistence  port.out 구현: JPA(쓰기·단건 조회), MyBatis(목록·검색·통계)
bootstrap                @SpringBootApplication, 환경 설정, 아키텍처·통합 테스트
db/ddl                   테이블 DDL 스크립트 (DBA 반영용)
```

의존 방향은 `adapter → application → domain`이며 다음 세 가지로 강제합니다.
1. **Maven 모듈 경계**: application은 adapter를 의존성으로 갖지 않으므로 컴파일 자체가 되지 않습니다.
2. **maven-enforcer**: domain 모듈에 Spring, eGovFrame, JPA, MyBatis 의존성이 들어오면 빌드가 실패합니다.
3. **ArchUnit** (`HexagonalArchitectureTest`): 패키지 간 의존 규칙을 테스트로 검증합니다.

패키지 루트는 `kr.go.agency.portal`입니다. 기관과 시스템이 정해지면 groupId와 패키지명을 함께 변경하세요.

## 영속성 규칙 (JPA + MyBatis)

| 작업 | 기술 | 반환 |
|---|---|---|
| 등록·수정·삭제, 단건 조회 | JPA | 도메인 객체 |
| 목록·검색·통계 등 복잡한 조회 | MyBatis (`@EgovMapper` + XML) | 조회 전용 DTO |

- **MyBatis Mapper에는 SELECT만 작성합니다.** `MyBatisSelectOnlyTest`가 mapper XML에서 쓰기 SQL을 검사합니다.
- JPA와 MyBatis는 같은 DataSource와 `JpaTransactionManager`를 공유하므로 한 트랜잭션에서 함께 동작합니다.
- 같은 트랜잭션에서 JPA로 저장한 직후 MyBatis로 조회해야 한다면, 조회 전에 `EntityManager.flush()`를 호출하세요. Hibernate는 MyBatis 쿼리 전에 자동으로 flush하지 않습니다.
- QueryDSL은 사용하지 않습니다.
- 페이징 SQL은 `OFFSET ... FETCH NEXT` 표준 문법(Oracle 12c+, Tibero, PostgreSQL, H2)을 사용합니다.

## 스키마 관리

- 테이블은 `db/ddl/*.sql` 스크립트로 형상관리하고, dev·prod 반영은 DBA가 직접 합니다.
- JPA `ddl-auto`는 local에서만 `create`이고, 그 외 환경은 `validate`입니다. 엔티티와 테이블이 다르면 기동 시 실패합니다.
- 테이블을 추가하거나 변경할 때는 DDL 스크립트와 JPA 엔티티를 함께 수정합니다.

## 환경(프로파일)

| 프로파일 | DB | 비고 |
|---|---|---|
| `local` (기본) | H2 인메모리 (Oracle 호환 모드) | 엔티티 기준 테이블 자동 생성 |
| `dev`, `prod` | 환경변수 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | DB 드라이버는 결정 후 `bootstrap/pom.xml`에 추가 |

운영 DB 드라이버가 정해지면 H2 의존성을 운영 빌드에서 제외하세요.
