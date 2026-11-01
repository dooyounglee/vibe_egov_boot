package kr.go.agency.portal.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 규칙: 쓰기(INSERT/UPDATE/DELETE)는 JPA만 사용하고, MyBatis Mapper에는 SELECT만 둔다.
 * 같은 트랜잭션에서 MyBatis로 쓰면 JPA 1차 캐시와 데이터가 어긋날 수 있기 때문이다.
 */
class MyBatisSelectOnlyTest {

    private static final Pattern WRITE_STATEMENT = Pattern.compile("<\\s*(insert|update|delete)[\\s>]",
            Pattern.CASE_INSENSITIVE);

    @Test
    void mapper_xml에는_SELECT만_있어야_한다() throws IOException {
        Resource[] mappers = new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml");
        assertThat(mappers).isNotEmpty();

        for (Resource mapper : mappers) {
            try (InputStream in = mapper.getInputStream()) {
                String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                assertThat(WRITE_STATEMENT.matcher(xml).find())
                        .as("쓰기 SQL은 JPA로 작성하세요: %s", mapper.getDescription())
                        .isFalse();
            }
        }
    }
}
