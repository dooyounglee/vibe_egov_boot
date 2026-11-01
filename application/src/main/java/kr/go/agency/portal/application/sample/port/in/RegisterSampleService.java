package kr.go.agency.portal.application.sample.port.in;

public interface RegisterSampleService {

    /**
     * @return 등록된 샘플의 식별자
     */
    Long register(RegisterSampleCommand command);

    record RegisterSampleCommand(String title, String content) {
    }
}
