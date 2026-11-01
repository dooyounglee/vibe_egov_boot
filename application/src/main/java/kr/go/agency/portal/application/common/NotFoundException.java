package kr.go.agency.portal.application.common;

/**
 * 요청한 대상을 찾을 수 없을 때 발생한다.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
