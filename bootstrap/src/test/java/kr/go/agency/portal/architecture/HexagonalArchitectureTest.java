package kr.go.agency.portal.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 헥사고날 구조의 의존 방향을 검증한다: adapter -> application -> domain
 */
@AnalyzeClasses(packages = "kr.go.agency.portal", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    private static final String DOMAIN = "kr.go.agency.portal.domain..";
    private static final String APPLICATION = "kr.go.agency.portal.application..";
    private static final String ADAPTER = "kr.go.agency.portal.adapter..";
    private static final String ADAPTER_IN = "kr.go.agency.portal.adapter.in..";
    private static final String ADAPTER_OUT = "kr.go.agency.portal.adapter.out..";

    @ArchTest
    static final ArchRule domain_is_framework_free = noClasses().that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "org.egovframe..", "jakarta..",
                    "org.hibernate..", "org.apache.ibatis..", "org.mybatis..",
                    APPLICATION, ADAPTER)
            .because("도메인은 프레임워크와 바깥 계층을 알지 못해야 한다");

    @ArchTest
    static final ArchRule application_does_not_depend_on_adapters = noClasses().that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat().resideInAnyPackage(
                    ADAPTER, "jakarta.persistence..", "org.hibernate..",
                    "org.apache.ibatis..", "org.mybatis..", "org.springframework.web..")
            .because("유스케이스는 포트만 알고 어댑터 구현 기술을 알지 못해야 한다");

    @ArchTest
    static final ArchRule inbound_adapters_do_not_use_outbound_adapters = noClasses().that().resideInAPackage(ADAPTER_IN)
            .should().dependOnClassesThat().resideInAPackage(ADAPTER_OUT);

    @ArchTest
    static final ArchRule outbound_adapters_do_not_use_inbound_adapters = noClasses().that().resideInAPackage(ADAPTER_OUT)
            .should().dependOnClassesThat().resideInAPackage(ADAPTER_IN);
}
