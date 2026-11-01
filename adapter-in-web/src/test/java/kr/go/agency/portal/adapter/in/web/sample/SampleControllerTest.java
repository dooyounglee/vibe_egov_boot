package kr.go.agency.portal.adapter.in.web.sample;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import kr.go.agency.portal.application.sample.port.in.GetSampleService;
import kr.go.agency.portal.application.sample.port.in.GetSampleService.SampleResult;
import kr.go.agency.portal.application.sample.port.in.RegisterSampleService;
import kr.go.agency.portal.application.sample.port.in.RegisterSampleService.RegisterSampleCommand;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;
import kr.go.agency.portal.domain.common.DomainException;

/**
 * HTTP ↔ Controller 검증. 유스케이스(port.in)는 Mock 으로 대체하고
 * 요청 바인딩·검증, 응답 형식, 예외 → 상태 코드 변환만 확인한다.
 */
@WebMvcTest(SampleController.class)
class SampleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterSampleService registerSampleService;

    @MockitoBean
    GetSampleService getSampleService;

    @MockitoBean
    SearchSampleService searchSampleService;

    @Test
    void 등록하면_201과_Location을_돌려준다() throws Exception {
        given(registerSampleService.register(any())).willReturn(7L);

        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "공지", "content": "본문"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/samples/7"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(7))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(registerSampleService).register(new RegisterSampleCommand("공지", "본문"));
    }

    @Test
    void 제목이_비어_있으면_서비스를_호출하지_않고_400() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        verifyNoInteractions(registerSampleService);
    }

    @Test
    void 도메인_규칙_위반은_400_INVALID_REQUEST() throws Exception {
        given(registerSampleService.register(any())).willThrow(new DomainException("제목은 필수입니다."));

        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"공지\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("제목은 필수입니다."));
    }

    @Test
    void 단건_조회_결과를_응답한다() throws Exception {
        given(getSampleService.get(1L))
                .willReturn(new SampleResult(1L, "공지", "본문", LocalDateTime.of(2026, 11, 1, 9, 0)));

        mockMvc.perform(get("/api/samples/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("공지"))
                .andExpect(jsonPath("$.data.createdAt").value("2026-11-01T09:00:00"));
    }

    @Test
    void 없는_샘플은_404() throws Exception {
        given(getSampleService.get(any())).willThrow(new NotFoundException("샘플을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/samples/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void 검색_파라미터를_조건으로_넘기고_페이지를_응답한다() throws Exception {
        given(searchSampleService.search(any())).willReturn(new PageResult<>(
                List.of(new SampleSummary(3L, "공지", LocalDateTime.of(2026, 11, 1, 9, 0))), 21, 2, 10));

        mockMvc.perform(get("/api/samples").param("keyword", " 공지 ").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCount").value(21))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.items[0].title").value("공지"));

        verify(searchSampleService).search(new SampleSearchCondition("공지", 2, 10));
    }

    @Test
    void 파라미터_형식이_틀리면_400() throws Exception {
        mockMvc.perform(get("/api/samples").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"));

        verifyNoInteractions(searchSampleService);
    }

    @Test
    void 검색어가_100자를_넘으면_400() throws Exception {
        mockMvc.perform(get("/api/samples").param("keyword", "가".repeat(101)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.error.message").value(startsWith("keyword: ")));

        verifyNoInteractions(searchSampleService);
    }

    @Test
    void 예상하지_못한_오류는_상세를_숨기고_500() throws Exception {
        given(getSampleService.get(any())).willThrow(new IllegalStateException("DB 접속 정보 노출"));

        mockMvc.perform(get("/api/samples/{id}", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.error.message").value("요청을 처리하는 중 오류가 발생했습니다."));
    }
}
