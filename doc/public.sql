/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 180000 (180000)
 Source Host           : localhost:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 180000 (180000)
 File Encoding         : 65001

 Date: 13/11/2025 17:12:35
*/


-- ----------------------------
-- Sequence structure for seq_config_ae_series_id
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."seq_config_ae_series_id";
CREATE SEQUENCE "public"."seq_config_ae_series_id" 
INCREMENT 1
MINVALUE  1
MAXVALUE 999999
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for seq_exam_predict_progress_id
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."seq_exam_predict_progress_id";
CREATE SEQUENCE "public"."seq_exam_predict_progress_id" 
INCREMENT 1
MINVALUE  1
MAXVALUE 999999
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for seq_exam_predict_result_id
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."seq_exam_predict_result_id";
CREATE SEQUENCE "public"."seq_exam_predict_result_id" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for ai_exam_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_exam_info";
CREATE TABLE "public"."ai_exam_info" (
  "accnum" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "his_exam_no" varchar(64) COLLATE "pg_catalog"."default",
  "patient_id" varchar(32) COLLATE "pg_catalog"."default",
  "modality" varchar(32) COLLATE "pg_catalog"."default",
  "exam_items" text COLLATE "pg_catalog"."default",
  "patient_name" varchar(64) COLLATE "pg_catalog"."default",
  "patient_sex" varchar(2) COLLATE "pg_catalog"."default",
  "patient_birthday" varchar(16) COLLATE "pg_catalog"."default",
  "exam_date_time" timestamptz(6),
  "req_dept_code" varchar(32) COLLATE "pg_catalog"."default",
  "req_dept_name" varchar(64) COLLATE "pg_catalog"."default",
  "tech_user_name" varchar(64) COLLATE "pg_catalog"."default",
  "tech_user_code" varchar(32) COLLATE "pg_catalog"."default",
  "exam_ae" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."ai_exam_info"."accnum" IS '检查编号（主键）';
COMMENT ON COLUMN "public"."ai_exam_info"."his_exam_no" IS 'HIS检查编号';
COMMENT ON COLUMN "public"."ai_exam_info"."patient_id" IS '患者ID';
COMMENT ON COLUMN "public"."ai_exam_info"."modality" IS '检查模态';
COMMENT ON COLUMN "public"."ai_exam_info"."exam_items" IS '检查项目';
COMMENT ON COLUMN "public"."ai_exam_info"."patient_name" IS '患者姓名';
COMMENT ON COLUMN "public"."ai_exam_info"."patient_sex" IS '患者性别';
COMMENT ON COLUMN "public"."ai_exam_info"."patient_birthday" IS '患者出生日期';
COMMENT ON COLUMN "public"."ai_exam_info"."exam_date_time" IS '检查时间';
COMMENT ON COLUMN "public"."ai_exam_info"."req_dept_code" IS '申请科室编码';
COMMENT ON COLUMN "public"."ai_exam_info"."req_dept_name" IS '申请科室名称';
COMMENT ON COLUMN "public"."ai_exam_info"."tech_user_name" IS '技师姓名';
COMMENT ON COLUMN "public"."ai_exam_info"."tech_user_code" IS '技师编码';
COMMENT ON COLUMN "public"."ai_exam_info"."exam_ae" IS '检查设备AE标题';
COMMENT ON TABLE "public"."ai_exam_info" IS 'AI检查信息表';

-- ----------------------------
-- Records of ai_exam_info
-- ----------------------------
INSERT INTO "public"."ai_exam_info" VALUES ('CT20251113001', 'HIS20251113001', 'PAT0001', 'CT', '脑部CT平扫', '张三', '男', '1990-01-15', '2025-11-13 08:30:00+08', 'DEPT001', '神经外科', '李四', 'TECH001', 'CT_AE001');
INSERT INTO "public"."ai_exam_info" VALUES ('MRI20251113002', 'HIS20251113002', 'PAT0002', 'MRI', '肺部MRI增强', '李四', '女', '1985-06-20', '2025-11-13 09:15:00+08', 'DEPT002', '呼吸内科', '王五', 'TECH002', 'MRI_AE002');
INSERT INTO "public"."ai_exam_info" VALUES ('DR20251113003', 'HIS20251113003', 'PAT0003', 'DR', '胸部DR正位', '王五', '男', '2000-03-10', '2025-11-13 10:00:00+08', 'DEPT003', '急诊科', '赵六', 'TECH003', 'DR_AE003');

-- ----------------------------
-- Table structure for config_ae_series
-- ----------------------------
DROP TABLE IF EXISTS "public"."config_ae_series";
CREATE TABLE "public"."config_ae_series" (
  "id" int8 NOT NULL,
  "ae" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "modality" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "target_key" varchar(128) COLLATE "pg_catalog"."default",
  "series_description" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."config_ae_series"."id" IS '主键ID';
COMMENT ON COLUMN "public"."config_ae_series"."ae" IS 'AE标识';
COMMENT ON COLUMN "public"."config_ae_series"."modality" IS '模态信息（如DICOM中的检查模态）';
COMMENT ON COLUMN "public"."config_ae_series"."target_key" IS '预测目标的key';
COMMENT ON COLUMN "public"."config_ae_series"."series_description" IS '序列描述';
COMMENT ON TABLE "public"."config_ae_series" IS 'AE序列配置表';

-- ----------------------------
-- Records of config_ae_series
-- ----------------------------
INSERT INTO "public"."config_ae_series" VALUES (1, 'CT_AE001', 'CT', 'crisisPredict_brain', '脑部CT危急值预测序列');
INSERT INTO "public"."config_ae_series" VALUES (2, 'MRI_AE002', 'MRI', 'crisisPredict_lung', '肺部MRI危急值预测序列');
INSERT INTO "public"."config_ae_series" VALUES (3, 'DR_AE003', 'DR', 'crisisPredict_chest', '胸部DR危急值预测序列');

-- ----------------------------
-- Table structure for exam_predict_progress
-- ----------------------------
DROP TABLE IF EXISTS "public"."exam_predict_progress";
CREATE TABLE "public"."exam_predict_progress" (
  "id" int8 NOT NULL,
  "accnum" varchar(64) COLLATE "pg_catalog"."default",
  "his_exam_no" varchar(64) COLLATE "pg_catalog"."default",
  "patient_id" varchar(32) COLLATE "pg_catalog"."default",
  "predict_type" varchar(64) COLLATE "pg_catalog"."default",
  "progress" varchar(32) COLLATE "pg_catalog"."default",
  "is_error" int4,
  "error_msg" text COLLATE "pg_catalog"."default",
  "progress_date" timestamptz(6)
)
;
COMMENT ON COLUMN "public"."exam_predict_progress"."id" IS '主键ID（唯一标识一条预测进度记录）';
COMMENT ON COLUMN "public"."exam_predict_progress"."accnum" IS '检查流水号（影像设备生成的唯一检查编号）';
COMMENT ON COLUMN "public"."exam_predict_progress"."his_exam_no" IS 'HIS系统检查编号（医院HIS系统的检查订单号）';
COMMENT ON COLUMN "public"."exam_predict_progress"."patient_id" IS '患者ID（医院分配的患者唯一标识）';
COMMENT ON COLUMN "public"."exam_predict_progress"."predict_type" IS 'AI预测类型（如：crisisPredict=危急值预测）';
COMMENT ON COLUMN "public"."exam_predict_progress"."progress" IS '预测进度状态（如：PENDING=待处理、PROCESSING=处理中、COMPLETED=完成、FAILED=失败）';
COMMENT ON COLUMN "public"."exam_predict_progress"."is_error" IS '是否出错（0=未出错，1=出错）';
COMMENT ON COLUMN "public"."exam_predict_progress"."error_msg" IS '错误信息（is_error=1时存储具体错误原因）';
COMMENT ON COLUMN "public"."exam_predict_progress"."progress_date" IS '进度更新时间（状态变化时的时间戳）';
COMMENT ON TABLE "public"."exam_predict_progress" IS 'AI检查预测进度表：记录AI预测任务的执行状态和进度';

-- ----------------------------
-- Records of exam_predict_progress
-- ----------------------------
INSERT INTO "public"."exam_predict_progress" VALUES (1, 'CT20251113001', 'HIS20251113001', 'PAT0001', 'crisisPredict', 'COMPLETED', 0, NULL, '2025-11-13 08:35:00+08');
INSERT INTO "public"."exam_predict_progress" VALUES (2, 'MRI20251113002', 'HIS20251113002', 'PAT0002', 'crisisPredict', 'COMPLETED', 0, NULL, '2025-11-13 09:20:00+08');
INSERT INTO "public"."exam_predict_progress" VALUES (3, 'DR20251113003', 'HIS20251113003', 'PAT0003', 'crisisPredict', 'FAILED', 1, '影像文件损坏，无法解析', '2025-11-13 10:05:00+08');

-- ----------------------------
-- Table structure for exam_predict_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."exam_predict_result";
CREATE TABLE "public"."exam_predict_result" (
  "id" int8 NOT NULL DEFAULT nextval('seq_exam_predict_result_id'::regclass),
  "accnum" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "his_exam_no" varchar(64) COLLATE "pg_catalog"."default",
  "patient_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "predict_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "predict_service" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "predict_desc" text COLLATE "pg_catalog"."default",
  "predict_result" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "structural_version" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'v1'::character varying
)
;
COMMENT ON COLUMN "public"."exam_predict_result"."id" IS '主键ID（唯一标识一条预测结果记录）';
COMMENT ON COLUMN "public"."exam_predict_result"."accnum" IS '检查流水号（影像设备生成的唯一检查编号）';
COMMENT ON COLUMN "public"."exam_predict_result"."his_exam_no" IS 'HIS系统检查编号（医院HIS系统的检查订单号）';
COMMENT ON COLUMN "public"."exam_predict_result"."patient_id" IS '患者ID（医院分配的患者唯一标识）';
COMMENT ON COLUMN "public"."exam_predict_result"."predict_type" IS 'AI预测类型（如：crisisPredict=危急值预测）';
COMMENT ON COLUMN "public"."exam_predict_result"."predict_service" IS '预测服务名称（具体执行预测的AI服务标识，如：brain_crisis_predict=脑部危急值预测）';
COMMENT ON COLUMN "public"."exam_predict_result"."predict_desc" IS '预测结果描述（AI判断的具体情况，如：脑出血、无危急病变）';
COMMENT ON COLUMN "public"."exam_predict_result"."predict_result" IS '预测结果标识（标准化状态：POSITIVE=阳性、NEGATIVE=阴性、UNCERTAIN=不确定）';
COMMENT ON COLUMN "public"."exam_predict_result"."structural_version" IS '数据结构版本（默认v1，用于兼容后续字段变更）';
COMMENT ON TABLE "public"."exam_predict_result" IS 'AI检查预测结果表：存储AI对医疗影像的最终预测结果';

-- ----------------------------
-- Records of exam_predict_result
-- ----------------------------
INSERT INTO "public"."exam_predict_result" VALUES (1, 'CT20251113001', 'HIS20251113001', 'PAT0001', 'crisisPredict', 'brain_crisis_predict', '脑部CT平扫可见右侧基底节区高密度影，考虑脑出血（危急值）', 'POSITIVE', 'v1');
INSERT INTO "public"."exam_predict_result" VALUES (2, 'MRI20251113002', 'HIS20251113002', 'PAT0002', 'crisisPredict', 'lung_crisis_predict', '肺部MRI增强未见明显危急病变，无出血、栓塞等征象', 'NEGATIVE', 'v1');

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."seq_config_ae_series_id"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."seq_exam_predict_progress_id"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."seq_exam_predict_result_id"', 2, true);

-- ----------------------------
-- Primary Key structure for table ai_exam_info
-- ----------------------------
ALTER TABLE "public"."ai_exam_info" ADD CONSTRAINT "pk_ai_exam_info" PRIMARY KEY ("accnum");

-- ----------------------------
-- Primary Key structure for table config_ae_series
-- ----------------------------
ALTER TABLE "public"."config_ae_series" ADD CONSTRAINT "config_ae_series_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table exam_predict_progress
-- ----------------------------
ALTER TABLE "public"."exam_predict_progress" ADD CONSTRAINT "exam_predict_progress_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table exam_predict_result
-- ----------------------------
ALTER TABLE "public"."exam_predict_result" ADD CONSTRAINT "idx_exam_predict_result_patientid" UNIQUE ("patient_id", "accnum", "predict_type");

-- ----------------------------
-- Primary Key structure for table exam_predict_result
-- ----------------------------
ALTER TABLE "public"."exam_predict_result" ADD CONSTRAINT "pk_exam_predict_result_id" PRIMARY KEY ("id");
