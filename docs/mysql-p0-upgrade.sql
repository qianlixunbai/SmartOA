-- ============================================================
-- SmartOA P0 2级审批流转 — MySQL 迁移脚本
-- 如果你的 Workbench 里已有 smartoa 库的旧表，执行此脚本升级
-- 如果是全新库，先跑完 JPA ddl-auto=update 再跑本脚本填充种子数据
-- ============================================================

-- 1. sys_user 表增加直属领导、部门总监字段
ALTER TABLE sys_user ADD COLUMN direct_leader_id BIGINT NULL COMMENT '直属领导ID';
ALTER TABLE sys_user ADD COLUMN department_head_id BIGINT NULL COMMENT '部门总监ID';

-- 2. leave_request 表增加审批步骤和当前审批人字段
ALTER TABLE leave_request ADD COLUMN approval_step INT NOT NULL DEFAULT 0 COMMENT '审批步骤(0=直属领导, 1=部门总监, 2=完成)';
ALTER TABLE leave_request ADD COLUMN current_approver_id BIGINT NULL COMMENT '当前审批人ID';

-- 3. approval_record 表增加审批步骤字段
ALTER TABLE approval_record ADD COLUMN approval_step INT NOT NULL DEFAULT 0 COMMENT '审批步骤(0=直属领导, 1=部门总监)';

-- 4. 种子数据（组织层级：王经理是直属领导，张总监/李总监是部门总监）
-- 如果已有数据请先备份或清空
DELETE FROM sys_user;
INSERT INTO sys_user (username, password, real_name, role, department, direct_leader_id, department_head_id) VALUES
  ('admin',     '123456', '王经理',   'MANAGER',  '技术部', NULL, 4),
  ('zhangsan',  '123456', '张三',     'EMPLOYEE', '技术部', 1, 4),
  ('lisi',      '123456', '李四',     'EMPLOYEE', '产品部', 1, 5),
  ('zongjian1', '123456', '张总监',   'MANAGER',  '技术部', NULL, NULL),
  ('zongjian2', '123456', '李总监',   'MANAGER',  '产品部', NULL, NULL);
