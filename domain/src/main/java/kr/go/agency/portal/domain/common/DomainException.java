package kr.go.agency.portal.domain.common;

/**
 * 도메인 규칙 위반을 나타내는 예외.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
