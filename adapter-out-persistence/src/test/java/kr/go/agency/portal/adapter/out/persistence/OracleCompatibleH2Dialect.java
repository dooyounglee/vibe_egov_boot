package kr.go.agency.portal.adapter.out.persistence;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.SqlTypes;

/**
 * 테스트 전용 방언. H2(Oracle 모드)는 운영 DDL의 NUMBER(19)를 NUMERIC 으로 보고하는데,
 * H2Dialect 는 Long(BIGINT)과 같은 타입으로 보지 않아 ddl-auto=validate 가 실패한다.
 * 실제 Oracle 에서는 타입명(NUMBER)이 일치해 통과하므로, 정수형 ↔ NUMERIC 만 동등하게 취급한다.
 */
public class OracleCompatibleH2Dialect extends H2Dialect {

    @Override
    public boolean equivalentTypes(int typeCode1, int typeCode2) {
        return super.equivalentTypes(typeCode1, typeCode2)
                || SqlTypes.isIntegral(typeCode1) && SqlTypes.isNumericOrDecimal(typeCode2)
                || SqlTypes.isNumericOrDecimal(typeCode1) && SqlTypes.isIntegral(typeCode2);
    }
}
