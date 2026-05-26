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
│   ├── controller/          # REST 控制器（3 个）
│   ├── service/             # 业务逻辑层（3 个）
│   ├── repository/          # JPA 数据访问（4 个）
│   ├── entity/              # 实体类（4 个）
│   └── dto/                 # 数据传输对象（2 个）
├── src/main/resources/
│   └── application.properties
├── frontend/                # Vue 3 前端
│   └── src/
│       ├── api/             # 接口封装
│       ├── stores/          # Pinia 状态管理
│       ├── router/          # 路由配置
│       ├── views/           # 页面组件（6 个）
│       ├── components/      # 共享组件
│       └── layouts/         # 布局组件
├── docs/
│   └── mysql-p0-upgrade.sql # 建库建表 + 种子数据
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

## 已实现功能（P0）

- [x] 用户登录（JWT + 角色区分）
- [x] 审批模板 CRUD
- [x] 请假申请提交
- [x] 硬编码二级审批流转（直属领导 → 部门总监）
- [x] 我的待办 / 已办 / 我提交的
- [x] 审批详情页（含审批记录时间线）

---

## 待实现（P1 / P2）

- [ ] 可配置多级审批引擎
- [ ] 同意 / 拒绝 / 撤回 / 转派四种操作
- [ ] 流程进度条（可视化）
- [ ] 审批历史时间线
- [ ] ECharts 统计分析
- [ ] 导出 Excel
- [ ] 流程可视化编辑器（拖拽节点）

---

## 许可证

MIT License

---

---

# SmartOA — シンプル OA 承認ワークフロー管理システム

> 企業向け OA 承認ワークフロー管理システム（P0 完了版） | Spring Boot 4 + Vue 3 + MyBatis-Plus + JWT

---

## プロジェクト概要

SmartOA は、企業の日常業務向けの**シンプルな承認ワークフロー管理システム**です。ユーザーログイン認証（JWT）、承認テンプレート管理、休暇申請の提出、ハードコードされた二段階承認フロー（直属の上司 → 部門ディレクター）、そして「保留中 / 完了 / 自分の提出」タスク管理をサポートします。

コア設計は「テンプレート設定 + 多段階承認 + フローエンジン」を中心に展開されています。現在の P0 フェーズではハードコードされた二段階承認を採用しており、将来的に設定可能な承認エンジンやビジュアルフローエディタへ拡張可能です。

---

## 技術スタック

| レイヤー | 技術 |
|----------|------|
| バックエンド | Spring Boot 4.0.6 |
| 永続化 | Spring Data JPA（Hibernate 7.2） |
| データベース | MySQL 8.0 |
| 認証 | JWT（jjwt 0.12.6） |
| フロントエンド | Vue 3（Composition API） |
| UI ライブラリ | Element Plus |
| ビルドツール | Vite 8 |
| 状態管理 | Pinia |
| ルーティング | Vue Router 4 |

---

## プロジェクト構成

```
smartoa/
├── src/main/java/com/smartoa/
│   ├── config/              # セキュリティ設定、CORS、JWT フィルター
│   ├── controller/          # REST コントローラー（3 ファイル）
│   ├── service/             # ビジネスロジック層（3 ファイル）
│   ├── repository/          # JPA データアクセス（4 ファイル）
│   ├── entity/              # エンティティクラス（4 ファイル）
│   └── dto/                 # データ転送オブジェクト（2 ファイル）
├── src/main/resources/
│   └── application.properties
├── frontend/                # Vue 3 フロントエンド
│   └── src/
│       ├── api/             # API クライアント
│       ├── stores/          # Pinia 状態管理
│       ├── router/          # ルーティング設定
│       ├── views/           # ページコンポーネント（6 ページ）
│       ├── components/      # 共有コンポーネント
│       └── layouts/         # レイアウトコンポーネント
├── docs/
│   └── mysql-p0-upgrade.sql # テーブル作成 + シードデータ
└── pom.xml
```

---

## データベース設計

| テーブル | 説明 |
|----------|------|
| `sys_user` | ユーザーテーブル（直属上司・部門ディレクターの関連付けを含む） |
| `approval_template` | 承認テンプレートテーブル |
| `leave_request` | 休暇申請テーブル（承認状態 + 現在の承認者によりフロー制御） |
| `approval_record` | 承認記録テーブル（操作ログ） |

5 名のシードユーザー（admin / zhangsan / lisi / zongjian1 / zongjian2）が事前登録されており、全員のパスワードは `123456` です。

---

## クイックスタート

### 環境要件

- Java 21+
- MySQL 8.0+
- Node.js 18+
- Maven 3.8+

### 1️⃣ データベース作成

```sql
CREATE DATABASE smartoa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

続いて `docs/mysql-p0-upgrade.sql` をインポートし、テーブル構造とシードデータを作成します。

### 2️⃣ バックエンド起動

```bash
./mvnw spring-boot:run
```

デフォルトポート：`8080`。

### 3️⃣ フロントエンド起動

```bash
cd frontend
npm install
npm run dev
```

デフォルトポート：`5173`。バックエンドへのプロキシ転送が設定済みです。

### 4️⃣ ログイン

ブラウザで `http://localhost:5173` を開き、以下のアカウントでログインしてください：

| ユーザー名 | パスワード | 役割 | 備考 |
|------------|------------|------|------|
| admin | 123456 | MANAGER | 技術部マネージャー |
| zhangsan | 123456 | EMPLOYEE | 一般社員 |

---

## 実装済み機能（P0）

- [x] ユーザーログイン（JWT + ロール区別）
- [x] 承認テンプレート CRUD
- [x] 休暇申請の提出
- [x] ハードコード二段階承認フロー（直属上司 → 部門ディレクター）
- [x] 保留中 / 完了 / 自分の提出タスク管理
- [x] 承認詳細ページ（承認記録タイムライン付き）

---

## 今後の予定（P1 / P2）

- [ ] 設定可能な多段階承認エンジン
- [ ] 承認 / 却下 / 取り消し / 転送の 4 アクション
- [ ] フロー進捗バー（可視化）
- [ ] 承認履歴タイムライン
- [ ] ECharts 統計分析
- [ ] Excel エクスポート
- [ ] ビジュアルフローエディタ（ノードのドラッグ＆ドロップ）

---

## ライセンス

MIT License
