package kr.go.agency.portal.adapter.in.web.survey;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import kr.go.agency.portal.adapter.in.web.common.ApiResponse;
import kr.go.agency.portal.application.common.PageResult;
import kr.go.agency.portal.application.survey.port.in.ChangeSurveyQuestionService;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService;
import kr.go.agency.portal.application.survey.port.in.GetSurveyService.SurveyResult;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService;
import kr.go.agency.portal.application.survey.port.in.RegisterSurveyService.RegisterSurveyCommand;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final RegisterSurveyService registerSurveyService;
    private final ChangeSurveyQuestionService changeSurveyQuestionService;
    private final GetSurveyService getSurveyService;
    private final SearchSurveyService searchSurveyService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> register(@Valid @RequestBody RegisterSurveyRequest request) {
        Long id = registerSurveyService.register(new RegisterSurveyCommand(request.title(), request.questions()));
        return ResponseEntity.created(URI.create("/api/surveys/" + id)).body(ApiResponse.ok(id));
    }

    @GetMapping("/{id}")
    public ApiResponse<SurveyResult> get(@PathVariable Long id) {
        return ApiResponse.ok(getSurveyService.get(id));
    }

    @GetMapping
    public ApiResponse<PageResult<SurveySummary>> search(
            @RequestParam(required = false) @Size(max = 100) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(searchSurveyService.search(new SurveySearchCondition(keyword, page, size)));
    }

    @PostMapping("/{id}/questions")
    public ApiResponse<Void> addQuestion(@PathVariable Long id, @Valid @RequestBody AddQuestionRequest request) {
        changeSurveyQuestionService.addQuestion(id, request.content());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public ApiResponse<Void> removeQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        changeSurveyQuestionService.removeQuestion(id, questionId);
        return ApiResponse.ok(null);
    }

    public record RegisterSurveyRequest(
            @NotBlank @Size(max = 100) String title,
            @Size(max = 20) List<@NotBlank @Size(max = 500) String> questions) {
    }

    public record AddQuestionRequest(@NotBlank @Size(max = 500) String content) {
    }
}
