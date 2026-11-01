package kr.go.agency.portal.application.sample.port.out;

import kr.go.agency.portal.domain.sample.Sample;

public interface SaveSamplePort {

    /**
     * @return 식별자가 부여된 샘플
     */
    Sample save(Sample sample);
}
