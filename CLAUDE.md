# SmartOA — 简易 OA 审批流管理系统

Spring Boot + Vue 3 + MyBatis-Plus，聚焦请假审批流程。

---

## 关于用户

- 大三学生，Java 学习中，目标赴日就职（富士通等日企）
- 主攻技术栈：Spring Boot + Java + MySQL，同时准备 N2 考试
- 开发工具：IntelliJ IDEA 2024.3、VS Code、Cursor
- C 盘 146GB，Android SDK 在 D:\android
- Claude Code 通过 DeepSeek API 后端运行（配置在 `C:\Users\28421\.claude\settings.json`）

## 交流方式（重要）

1. **始终用中文**交流
2. 讲解代码时**详细解释逻辑和底层原理**
3. 主动关联**日企面试考点**（Java 基础、Spring Boot 常见问题、SQL 优化、设计模式等）
4. 先中文详解，再用**日语总结**（标注罗马音）
5. 写代码时**注释用日文**，遵循日企严谨编码风格（命名清晰、禁止魔法数字等）
6. 日语格式：`日本語（にほんご / nihongo）`，汉字后跟假名+罗马字

---

## 技术栈

- 后端：Spring Boot 4.0.6 + Java 21 + MyBatis-Plus + MySQL
- 前端：Vue 3.5 + Vite 6 + Element Plus 2.9 + Pinia + Axios
- 认证：JWT (jjwt 0.12.6)

## 启动方式

```bash
# 后端
./mvnw spring-boot:run          # → localhost:8080

# 前端
cd frontend && npm run dev       # → localhost:5173（proxy /api → 8080）
```

## 当前进度

- **P0/P1 已完成**：JWT 认证、可配置审批引擎（approval_node 表驱动）、6 表架构、4 审批动作（同意/拒绝/撤回/转派）、Excel 导出、ECharts 统计
- **P2 待做**：流程节点可视化编辑器、条件分支、并行审批、超时自动升级

## 测试账号

- admin / 123456（经理）
- zhangsan / 123456（员工）

## 全局配置

Claude Code 使用 DeepSeek API 后端（`C:\Users\28421\.claude\settings.json`）：
- `ANTHROPIC_BASE_URL` = `https://api.deepseek.com/anthropic`
- 默认模型：`deepseek-v4-pro`、Haiku：`deepseek-v4-flash`

## 磁盘清理偏好

- **绝对不能动**：腾讯系（微信/QQ/WeGame）、Chrome 用户数据、JetBrains IDE 缓存、MySQL 数据
- **可以删**：旧版本软件缓存、游戏缓存/着色器、更新包、NVIDIA 驱动下载缓存、Windows Temp
- 删除前先解释每个项目是什么，让用户决定
