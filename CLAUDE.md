# SmartOA — 简易 OA 审批流管理系统

Spring Boot + Vue 3 + MyBatis-Plus，聚焦请假审批流程。

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
