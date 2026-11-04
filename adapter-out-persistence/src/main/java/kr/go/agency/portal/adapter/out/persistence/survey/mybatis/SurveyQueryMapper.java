package kr.go.agency.portal.adapter.out.persistence.survey.mybatis;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.EgovMapper;

import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySearchCondition;
import kr.go.agency.portal.application.survey.port.in.SearchSurveyService.SurveySummary;

/**
 * 조회 전용 Mapper. SQL은 resources/mapper/survey/SurveyQueryMapper.xml 에 있다.
 * 규칙: MyBatis Mapper에는 SELECT만 둔다 (쓰기는 JPA).
 */
@EgovMapper
interface SurveyQueryMapper {

    List<SurveySummary> selectSurveyList(SurveySearchCondition condition);

    long selectSurveyCount(SurveySearchCondition condition);
}
