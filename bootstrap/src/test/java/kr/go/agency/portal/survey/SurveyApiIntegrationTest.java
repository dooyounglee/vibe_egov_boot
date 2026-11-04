package kr.go.agency.portal.survey;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/**
 * Web -> Service -> JPA(1:N 등록·변경·단건 조회) / MyBatis(목록) 전체 흐름을 local(H2) 프로파일로 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SurveyApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 설문을_등록하고_문항을_추가_삭제할_수_있다() throws Exception {
        String location = mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "통합테스트 설문", "questions": ["첫 번째", "두 번째"]}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        String body = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        Number firstQuestionId = JsonPath.read(body, "$.data.questions[0].id");

        mockMvc.perform(post(location + "/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"세 번째\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete(location + "/questions/{questionId}", firstQuestionId.longValue()))
                .andExpect(status().isOk());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions.length()").value(2))
                .andExpect(jsonPath("$.data.questions[0].content").value("두 번째"))
                .andExpect(jsonPath("$.data.questions[1].content").value("세 번째"));

        // JPA로 쓴 결과를 MyBatis 목록 조회(문항 수 집계)로 확인
        mockMvc.perform(get("/api/surveys").param("keyword", "통합테스트 설문"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andExpect(jsonPath("$.data.items[0].questionCount").value(2));
    }

    @Test
    void 없는_설문을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/surveys/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}
