# Career Plan Frontend

职业发展计划项目的前端应用，基于 React + Vite + Ant Design。

## 技术栈

- **React 18** - UI 框架
- **Vite 5** - 构建工具
- **React Router 6** - 页面路由
- **Ant Design 5** - UI 组件库
- **Axios** - HTTP 客户端

## 项目结构

```
frontend/
├── index.html                 # HTML 入口
├── package.json               # 依赖配置
├── vite.config.js             # Vite 配置
├── src/
│   ├── index.js               # JS 入口
│   ├── App.jsx                # 主应用组件
│   ├── pages/
│   │   ├── Dashboard.jsx      # 主页（Dashboard）
│   │   └── Dashboard.css      # Dashboard 样式
│   ├── components/
│   │   ├── ResponsiveNavbar.jsx   # 响应式导航栏（任务 4）
│   │   ├── ResponsiveNavbar.css
│   │   ├── TaskCard.jsx           # 响应式任务卡片（任务 4）
│   │   ├── TaskCard.css
│   │   ├── ResponsiveTable.jsx    # 响应式表格（任务 4）
│   │   └── ResponsiveTable.css
│   └── styles/
│       ├── responsive.css     # 响应式主样式（任务 4）
│       └── mobile.css         # 移动端专用样式（任务 4）
└── .gitignore
```

## 功能特性

### ✅ 已完成（任务 9）

1. **项目结构搭建**
   - Vite + React 项目配置
   - 路由配置（React Router）
   - Ant Design 集成

2. **主应用组件（App.jsx）**
   - 全局布局
   - 响应式导航栏集成
   - 路由配置

3. **Dashboard 主页**
   - 快速操作区（创建计划、创建任务）
   - 统计卡片（总计划数、进行中、总任务数、完成率）
   - 计划列表（使用 ResponsiveTable）
   - 最近任务（使用 TaskCard）

4. **响应式支持**
   - 整合任务 4 的响应式组件
   - 移动端和桌面端适配
   - 性能优化（代码分割）

## 开发指南

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

输出目录：`dist/`

### 代码检查

```bash
npm run lint
```

## 页面路由

| 路径 | 组件 | 描述 |
|------|------|------|
| `/` | Dashboard | 主页（默认） |
| `/dashboard` | Dashboard | 主页 |
| `/plans` | 占位 | 计划页面（待开发） |
| `/tasks` | 占位 | 任务页面（待开发） |
| `/progress` | 占位 | 进度页面（待开发） |
| `/profile` | 占位 | 个人中心（待开发） |

## 组件说明

### ResponsiveNavbar
响应式导航栏组件，支持：
- 桌面端：完整导航菜单
- 移动端：汉堡菜单 + 下拉菜单

### TaskCard
响应式任务卡片组件，显示：
- 任务标题
- 任务描述
- 状态（todo/doing/completed）
- 优先级（high/medium/low）
- 截止日期

### ResponsiveTable
响应式表格组件，支持：
- 桌面端：完整表格
- 移动端：卡片式布局

## 性能优化

### 代码分割
```js
// vite.config.js
manualChunks: {
  'react-vendor': ['react', 'react-dom', 'react-router-dom'],
  'antd-vendor': ['antd']
}
```

### 懒加载
未来可以添加路由级别的代码分割：
```jsx
const Dashboard = lazy(() => import('./pages/Dashboard'));
```

## 下一步计划

- [ ] API 集成（连接后端）
- [ ] 计划管理页面
- [ ] 任务管理页面
- [ ] 进度可视化图表
- [ ] 用户认证集成
- [ ] 单元测试
- [ ] E2E 测试

## 分支

- `feature/frontend-integration` - 任务 9 开发分支
- `feature/api-plans` - 主功能分支
- `feature/mobile-responsive` - 任务 4（移动端响应式）

---

**创建时间**: 2026-03-17  
**负责人**: alice  
**任务**: 任务 9 - 前端页面整合
