# 职业发展计划 2026 - 前端

## 技术栈

- **React 18** - UI 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Ant Design 5** - UI 组件库
- **Recharts** - 图表库

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览构建结果

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── components/          # 可复用组件
│   │   ├── TaskBoard.tsx    # 任务看板
│   │   ├── TaskCard.tsx     # 任务卡片
│   │   └── ProgressBar.tsx  # 进度条
│   ├── pages/               # 页面组件
│   │   └── Dashboard.tsx    # 主页面
│   ├── types/               # TypeScript 类型定义
│   │   └── task.ts          # 任务相关类型
│   ├── styles/              # 全局样式
│   │   └── index.css
│   ├── App.tsx              # 应用根组件
│   └── main.tsx             # 入口文件
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 功能特性

- ✅ 任务看板（Kanban）- 拖拽任务切换状态
- ✅ 任务卡片 - 显示任务详情、进度、优先级
- ✅ 可编辑任务 - 点击编辑修改任务信息
- ✅ 进度追踪 - 整体完成率和统计
- ✅ 响应式设计 - 适配不同屏幕尺寸

## 待开发功能

- [ ] 日历视图
- [ ] 团队成员管理
- [ ] 任务筛选和搜索
- [ ] 后端 API 集成
- [ ] 用户认证

## 开发规范

- 使用 TypeScript 严格模式
- 组件采用函数式 + Hooks
- 样式使用 CSS + Ant Design 组件
- 代码提交前运行 lint 检查

## 协作说明

本分支 `feature/alice-frontend` 由 @alice 开发，完成后将创建 Pull Request 合并到 main 分支。
