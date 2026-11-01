package kr.go.agency.portal.adapter.out.persistence.sample.mybatis;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.EgovMapper;

import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSearchCondition;
import kr.go.agency.portal.application.sample.port.in.SearchSampleService.SampleSummary;

/**
 * 조회 전용 Mapper. SQL은 resources/mapper/sample/SampleQueryMapper.xml 에 있다.
 * 규칙: MyBatis Mapper에는 SELECT만 둔다 (쓰기는 JPA).
 */
@EgovMapper
interface SampleQueryMapper {

    List<SampleSummary> selectSampleList(SampleSearchCondition condition);

    long selectSampleCount(SampleSearchCondition condition);
}
