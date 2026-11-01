package kr.go.agency.portal.adapter.in.web.sample;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import kr.go.agency.portal.application.sample.port.in.GetSampleService;
import kr.go.agency.portal.application.sample.port.in.GetSampleService.SampleResult;
import kr.go.agency.portal.application.sample.port.in.RegisterSampleService;
import kr.go.agency.portal.application.sample.port.in.RegisterSampleService.RegisterSampleCommand;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private final RegisterSampleService registerSampleService;
    private final GetSampleService getSampleService;
    private final SearchSampleService searchSampleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> register(@Valid @RequestBody RegisterSampleRequest request) {
        Long id = registerSampleService.register(new RegisterSampleCommand(request.title(), request.content()));
        return ResponseEntity.created(URI.create("/api/samples/" + id)).body(ApiResponse.ok(id));
    }

    @GetMapping("/{id}")
    public ApiResponse<SampleResult> get(@PathVariable Long id) {
        return ApiResponse.ok(getSampleService.get(id));
    }

    @GetMapping
    public ApiResponse<PageResult<SampleSummary>> search(
            @RequestParam(required = false) @Size(max = 100) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(searchSampleService.search(new SampleSearchCondition(keyword, page, size)));
    }

    public record RegisterSampleRequest(
            @NotBlank @Size(max = 100) String title,
            @Size(max = 4000) String content) {
    }
}
