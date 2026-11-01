package kr.go.agency.portal.sample;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web -> Service -> JPA(등록·단건 조회) / MyBatis(검색) 전체 흐름을 local(H2) 프로파일로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SampleApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 등록하고_단건조회와_검색을_할_수_있다() throws Exception {
        String location = mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "통합테스트 공지", "content": "본문"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("통합테스트 공지"))
                .andExpect(jsonPath("$.data.content").value("본문"));

        mockMvc.perform(get("/api/samples").param("keyword", "통합테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.items[0].title").value("통합테스트 공지"));
    }

    @Test
    void 제목이_없으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }

    @Test
    void 파라미터_형식이_틀리면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/samples").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));
    }

    @Test
    void 없는_샘플을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/samples/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}
