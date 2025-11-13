CREATE SEQUENCE IF NOT EXISTS seq_config_ae_series_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 999999  -- PostgreSQL BIGINT 最大值
    START WITH 1
    CACHE 1
    NO CYCLE;
-- 创建配置表（不含字段内联注释，避免语法错误）
CREATE TABLE IF NOT EXISTS config_ae_series (
                                                id BIGINT NOT NULL PRIMARY KEY,
                                                ae VARCHAR(64) NOT NULL,
                                                modality VARCHAR(32) NOT NULL,
                                                target_key VARCHAR(128),
                                                series_description VARCHAR(255)
);

-- 单独为表和字段添加注释（PostgreSQL推荐方式）
COMMENT ON TABLE config_ae_series IS 'AE序列配置表';
COMMENT ON COLUMN config_ae_series.id IS '主键ID';
COMMENT ON COLUMN config_ae_series.ae IS 'AE标识';
COMMENT ON COLUMN config_ae_series.modality IS '模态信息（如DICOM中的检查模态）';
COMMENT ON COLUMN config_ae_series.target_key IS '预测目标的key';
COMMENT ON COLUMN config_ae_series.series_description IS '序列描述';



-- 2. 创建表结构（不含任何注释）
CREATE TABLE ai_exam_info (
                              accnum VARCHAR(64) NOT NULL,
                              his_exam_no VARCHAR(64),
                              patient_id VARCHAR(32),
                              modality VARCHAR(32),
                              exam_items TEXT,
                              patient_name VARCHAR(64),
                              patient_sex VARCHAR(2),
                              patient_birthday VARCHAR(16),
                              exam_date_time TIMESTAMPTZ,
                              req_dept_code VARCHAR(32),
                              req_dept_name VARCHAR(64),
                              tech_user_name VARCHAR(64),
                              tech_user_code VARCHAR(32),
                              exam_ae VARCHAR(64),
                              CONSTRAINT pk_ai_exam_info PRIMARY KEY (accnum)
);

-- 3. 单独添加表注释（必须在表创建后执行）
COMMENT ON TABLE ai_exam_info IS 'AI检查信息表';

-- 4. 单独添加字段注释（字段必须存在，且名称与表名对应）
COMMENT ON COLUMN ai_exam_info.accnum IS '检查编号（主键）';
COMMENT ON COLUMN ai_exam_info.his_exam_no IS 'HIS检查编号';
COMMENT ON COLUMN ai_exam_info.patient_id IS '患者ID';
COMMENT ON COLUMN ai_exam_info.modality IS '检查模态';
COMMENT ON COLUMN ai_exam_info.exam_items IS '检查项目';
COMMENT ON COLUMN ai_exam_info.patient_name IS '患者姓名';
COMMENT ON COLUMN ai_exam_info.patient_sex IS '患者性别';
COMMENT ON COLUMN ai_exam_info.patient_birthday IS '患者出生日期';
COMMENT ON COLUMN ai_exam_info.exam_date_time IS '检查时间';
COMMENT ON COLUMN ai_exam_info.req_dept_code IS '申请科室编码';
COMMENT ON COLUMN ai_exam_info.req_dept_name IS '申请科室名称';
COMMENT ON COLUMN ai_exam_info.tech_user_name IS '技师姓名';
COMMENT ON COLUMN ai_exam_info.tech_user_code IS '技师编码';
COMMENT ON COLUMN ai_exam_info.exam_ae IS '检查设备AE标题';



-- 创建序列（对应实体类@KeySequence注解）
CREATE SEQUENCE IF NOT EXISTS seq_exam_predict_progress_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 999999
    START WITH 1
    CACHE 1
    NO CYCLE;

-- 创建表结构（不含内联注释，避免语法错误）
CREATE TABLE IF NOT EXISTS exam_predict_progress (
                                                     id BIGINT NOT NULL primary key,
                                                     accnum VARCHAR(64) ,
                                                     his_exam_no VARCHAR(64) ,
                                                     patient_id VARCHAR(32) ,
                                                     predict_type VARCHAR(64),
                                                     progress VARCHAR(32)  ,
                                                     is_error INTEGER  ,
                                                     error_msg TEXT ,
                                                     progress_date TIMESTAMPTZ
);

-- 单独添加表注释
COMMENT ON TABLE exam_predict_progress IS '检查预测进度表';

-- 单独添加字段注释（严格匹配表和字段名）
COMMENT ON COLUMN exam_predict_progress.id IS '主键ID（自增序列）';
COMMENT ON COLUMN exam_predict_progress.accnum IS '检查编号';
COMMENT ON COLUMN exam_predict_progress.his_exam_no IS 'HIS检查编号';
COMMENT ON COLUMN exam_predict_progress.patient_id IS '患者ID';
COMMENT ON COLUMN exam_predict_progress.predict_type IS 'AI预测类型';
COMMENT ON COLUMN exam_predict_progress.progress IS '进度状态';
COMMENT ON COLUMN exam_predict_progress.is_error IS '是否错误（0：否，1：是）';
COMMENT ON COLUMN exam_predict_progress.error_msg IS '错误信息';
COMMENT ON COLUMN exam_predict_progress.progress_date IS '进度更新时间'
