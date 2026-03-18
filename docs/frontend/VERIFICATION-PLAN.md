# 前端代码验证方案

**版本**: 2.0 | **时间**: 2026-03-17 | **负责人**: @alice, @qa-tester, @career-worker

---

## 验证目标

**验证对象**: 前端交付物 (系统集成、前端功能增强、响应式布局)

**验证内容**: 代码质量、功能完整性、测试覆盖率、性能指标、用户体验、响应式兼容性

---

## 验证方案

### 1. 单元测试 (Jest + React Testing Library)

**位置**: frontend/src/__tests__/

**测试文件**:
- components/TaskBoard.test.js
- components/TaskCard.test.js
- components/ResponsiveNavbar.test.js
- components/ResponsiveTable.test.js
- components/CalendarView.test.js
- pages/Dashboard.test.js
- pages/Login.test.js

**运行**: npm test -- --coverage  
**目标**: ≥ 80%

---

### 2. E2E 测试 (Playwright)

**位置**: frontend/e2e/

**场景**: 登录流程、任务 CRUD、日历视图、响应式布局

**运行**: npx playwright test

---

### 3. 代码质量检查

- ESLint: npm run lint
- Prettier: npm run format:check
- TypeScript: npm run type-check

---

### 4. 性能测试

**Lighthouse**: Performance ≥ 90, Accessibility ≥ 90

**Web Vitals**: LCP ≤ 2.5s, FID ≤ 100ms, CLS ≤ 0.1

---

### 5. 手动验证

**功能**: 登录/退出、任务看板、日历视图、数据图表、响应式布局

**体验**: 加载速度 ≤ 2s、交互反馈、错误处理、可访问性

---

### 6. 响应式测试 (新增)

**测试设备**:
- 手机：iPhone 14 Pro, iPhone SE, Samsung Galaxy S23
- 平板：iPad Pro, iPad Air
- 桌面：MacBook Pro, Dell XPS, iMac

**测试断点**:
- 移动端：< 768px
- 平板端：768px - 1023px
- 桌面端：≥ 1024px

**验收标准**:
- ✅ 触摸目标 ≥ 44px
- ✅ 字体大小适配（移动端 16px 防止 iOS 缩放）
- ✅ 布局无横向滚动（除非预期）
- ✅ 图片响应式缩放
- ✅ 表单输入友好

**测试报告**: `frontend/RESPONSIVE-TEST-REPORT.md`

---

## 任务分配

### @alice 请创建 (截止：2026-03-17 18:00)

1. 单元测试 (P0)
2. E2E 测试 (P0)
3. 代码质量检查 (P1)
4. 性能测试 (P2)
5. 更新任务 markdown

### @career-worker 已完成 (2026-03-17)

1. ✅ 响应式导航栏组件
2. ✅ 响应式任务卡片组件
3. ✅ 响应式表格组件
4. ✅ 响应式样式文件 (responsive.css, mobile.css)
5. ✅ 响应式测试报告

---

## 验证报告模板

**位置**: frontend/tests/report.md

**内容**: 单元测试覆盖率、E2E 测试结果、代码质量、性能指标、总体评估

**响应式测试报告**: `frontend/RESPONSIVE-TEST-REPORT.md`

---

## 时间表

| 时间 | 任务 | 负责人 |
|------|------|--------|
| 19:25-19:45 | 单元测试 | alice |
| 19:45-20:05 | E2E 测试 | alice |
| 20:05-20:20 | 代码检查 | alice |
| 20:20-20:40 | qa-tester 验证 | qa-tester |
| 09:45-10:30 | 响应式实现 | career-worker |
| 10:30-11:00 | 响应式测试 | career-worker |

---

**最后更新**: 2026-03-17 10:30
