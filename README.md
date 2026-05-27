# SmartOA — 簡易 OA 審批流管理系統

> 企业级 OA 审批流管理系统（ P0 完成版 ） | Spring Boot 4 + Vue 3 + MyBatis-Plus + JWT

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen" alt="Spring Boot 4"/>
  <img src="https://img.shields.io/badge/Vue-3-4FC08D" alt="Vue 3"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL 8"/>
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License"/>
</p>

---

## 项目简介

SmartOA 是一个面向企业日常办公的**简易审批流管理系统**，支持用户登录认证（JWT）、审批模板管理、请假申请提交、硬编码二级审批流转（直属领导 → 部门总监），以及待办/已办/我提交的审批任务管理。

核心设计围绕"模板配置 + 多级审批 + 流程引擎"展开，当前 P0 阶段采用硬编码二级审批，后续可扩展为可配置审批引擎与可视化流程编辑器。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.0.6 |
| 持久层 | Spring Data JPA（Hibernate 7.2） |
| 数据库 | MySQL 8.0 |
| 认证鉴权 | JWT（jjwt 0.12.6） |
| 前端框架 | Vue 3（Composition API） |
| UI 组件库 | Element Plus |
| 构建工具 | Vite 8 |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |

---

## 项目结构

```
smartoa/
├── src/main/java/com/smartoa/
│   ├── config/              # 安全配置、CORS、JWT 过滤器
│   ├── controller/          # REST 控制器（5 个）
│   ├── service/             # 业务逻辑层（5 个）
│   ├── repository/          # 数据访问层（6 个）
│   ├── entity/              # 实体类（6 个）
│   └── dto/                 # 数据传输对象
├── src/main/resources/
│   └── application.properties
├── frontend/                # Vue 3 前端
│   └── src/
│       ├── api/             # 接口封装
│       ├── stores/          # Pinia 状态管理
│       ├── router/          # 路由配置
│       ├── views/           # 页面组件（7 个）
│       ├── components/      # 共享组件
│       └── layouts/         # 布局组件
├── docs/
│   ├── mysql-p0-upgrade.sql # 建库建表 + 种子数据
│   └── mysql-p1-upgrade.sql # P1 新增表 + 种子数据
└── pom.xml
```

---

## 数据库设计

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表（含直属领导、部门总监关联） |
| `approval_template` | 审批模板表 |
| `leave_request` | 请假申请表（审批状态 + 当前审批人驱动流转） |
| `approval_record` | 审批记录表（操作日志） |

预置 5 个种子用户（admin / zhangsan / lisi / zongjian1 / zongjian2），默认密码均为 `123456`。

---

## 快速启动

### 环境要求

- Java 21+
- MySQL 8.0+
- Node.js 18+
- Maven 3.8+

### 1️⃣ 建库

```sql
CREATE DATABASE smartoa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后导入 `docs/mysql-p0-upgrade.sql` 创建表结构和种子数据。

### 2️⃣ 启动后端

```bash
./mvnw spring-boot:run
```

默认端口 `8080`。

### 3️⃣ 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认端口 `5173`，已配置代理转发到后端。

### 4️⃣ 登录

浏览器打开 `http://localhost:5173`，使用以下账户登录：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | MANAGER | 技术部经理 |
| zhangsan | 123456 | EMPLOYEE | 普通员工 |

---

## 已实现功能（P0 + P1）

### P0 基础功能

- [x] 用户登录（JWT + 角色区分）
- [x] 审批模板 CRUD
- [x] 请假申请提交
- [x] 硬编码二级审批流转
- [x] 我的待办 / 已办 / 我提交的
- [x] 审批详情页

### P1 升级功能

- [x] 6 张数据库表（+ `approval_node`、`template_field`）
- [x] 可配置多级审批引擎（动态节点遍历，支持任意层级）
- [x] 同意 / 拒绝 / 撤回 / 转派四种操作
- [x] 审批节点配置 UI（模板编辑时可添加/删除/排序节点）
- [x] 流程进度条（`el-steps`，动态节点）
- [x] 审批历史时间线（`el-timeline`，颜色标注操作类型）
- [x] ECharts 统计图表（模板平均审批时长 + 各模板使用量）
- [x] 后端 Excel 导出（Apache POI）

---

## 待实现（P2）

- [ ] 前端 Excel 导出按钮（后端接口已完成）
- [ ] 动态表单渲染（根据 `template_field` 自动生成字段）
- [ ] `template_field` 后端 CRUD 接口
- [ ] 流程可视化编辑器（拖拽节点 + 连线）
- [ ] 审批条件分支（如金额 > 10000 自动加财务审批）
- [ ] 并行审批节点 + 抄送人机制
- [ ] 超时自动升级（`@Scheduled`）

---

## 许可证

MIT License

