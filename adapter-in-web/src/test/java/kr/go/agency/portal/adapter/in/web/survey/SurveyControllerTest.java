package kr.go.agency.portal.adapter.in.web.survey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.go.agency.portal.application.common.NotFoundException;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.ChangeSurveyQuestionService;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService.QuestionResult;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService.SurveyResult;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService.RegisterSurveyCommand;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;
import kr.go.agency.portal.domain.common.DomainException;

/**
 * HTTP ↔ Controller 검증. 유스케이스(port.in)는 Mock 으로 대체한다.
 */
@WebMvcTest(SurveyController.class)
class SurveyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterSurveyService registerSurveyService;

    @MockitoBean
    ChangeSurveyQuestionService changeSurveyQuestionService;

    @MockitoBean
    GetSurveyService getSurveyService;

    @MockitoBean
    SearchSurveyService searchSurveyService;

    @Test
    void 검색_파라미터를_조건으로_넘기고_문항_수를_포함한_페이지를_응답한다() throws Exception {
        given(searchSurveyService.search(any())).willReturn(new PageResult<>(
                List.of(new SurveySummary(3L, "만족도 조사", 2L, LocalDateTime.of(2026, 11, 1, 9, 0))), 11, 2, 10));

        mockMvc.perform(get("/api/surveys").param("keyword", " 만족도 ").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCount").value(11))
                .andExpect(jsonPath("$.data.items[0].title").value("만족도 조사"))
                .andExpect(jsonPath("$.data.items[0].questionCount").value(2));

        verify(searchSurveyService).search(new SurveySearchCondition("만족도", 2, 10));
    }

    @Test
    void 문항과_함께_등록하면_201과_Location을_돌려준다() throws Exception {
        given(registerSurveyService.register(any())).willReturn(3L);

        mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "만족도 조사", "questions": ["첫 번째", "두 번째"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/surveys/3"))
                .andExpect(jsonPath("$.data").value(3));

        verify(registerSurveyService).register(new RegisterSurveyCommand("만족도 조사", List.of("첫 번째", "두 번째")));
    }

    @Test
    void 빈_문항이_있으면_서비스를_호출하지_않고_400() throws Exception {
        mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "만족도 조사", "questions": ["첫 번째", " "]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        verifyNoInteractions(registerSurveyService);
    }

    @Test
    void 단건_조회는_문항_목록을_포함한다() throws Exception {
        given(getSurveyService.get(1L)).willReturn(new SurveyResult(1L, "만족도 조사",
                List.of(new QuestionResult(10L, "첫 번째"), new QuestionResult(11L, "두 번째")),
                LocalDateTime.of(2026, 11, 1, 9, 0)));

        mockMvc.perform(get("/api/surveys/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("만족도 조사"))
                .andExpect(jsonPath("$.data.questions.length()").value(2))
                .andExpect(jsonPath("$.data.questions[1].id").value(11))
                .andExpect(jsonPath("$.data.questions[1].content").value("두 번째"));
    }

    @Test
    void 문항을_추가한다() throws Exception {
        mockMvc.perform(post("/api/surveys/{id}/questions", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"새 문항\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(changeSurveyQuestionService).addQuestion(1L, "새 문항");
    }

    @Test
    void 문항을_삭제한다() throws Exception {
        mockMvc.perform(delete("/api/surveys/{id}/questions/{questionId}", 1, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(changeSurveyQuestionService).removeQuestion(1L, 10L);
    }

    @Test
    void 없는_문항_삭제는_400_INVALID_REQUEST() throws Exception {
        willThrow(new DomainException("존재하지 않는 문항입니다."))
                .given(changeSurveyQuestionService).removeQuestion(anyLong(), anyLong());

        mockMvc.perform(delete("/api/surveys/{id}/questions/{questionId}", 1, 99))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void 없는_설문은_404() throws Exception {
        given(getSurveyService.get(any())).willThrow(new NotFoundException("설문을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/surveys/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}
