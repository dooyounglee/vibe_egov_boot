-- =============================================================
-- TB_SAMPLE : 샘플 (헥사고날 구조 예시)
-- 반영 : DBA 수동 반영 (dev / prod)
-- 기준 : Oracle / Tibero 문법. 다른 DBMS는 타입을 맞춰 작성
-- 엔티티 : SampleJpaEntity (ddl-auto=validate 로 일치 여부 검증)
-- =============================================================

CREATE SEQUENCE SEQ_SAMPLE START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE TB_SAMPLE (
    SAMPLE_ID   NUMBER(19)      NOT NULL,
    TITLE       VARCHAR2(100)   NOT NULL,
    CONTENT     VARCHAR2(4000),
    CREATED_AT  TIMESTAMP       NOT NULL,
    CONSTRAINT PK_TB_SAMPLE PRIMARY KEY (SAMPLE_ID)
);

CREATE INDEX IX_TB_SAMPLE_01 ON TB_SAMPLE (CREATED_AT);

COMMENT ON TABLE  TB_SAMPLE            IS '샘플';
COMMENT ON COLUMN TB_SAMPLE.SAMPLE_ID  IS '샘플ID';
COMMENT ON COLUMN TB_SAMPLE.TITLE      IS '제목';
COMMENT ON COLUMN TB_SAMPLE.CONTENT    IS '내용';
COMMENT ON COLUMN TB_SAMPLE.CREATED_AT IS '등록일시';
