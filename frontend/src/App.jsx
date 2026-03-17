import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ResponsiveNavbar from './components/ResponsiveNavbar';
import Dashboard from './pages/Dashboard';

/**
 * 主应用组件
 * 配置路由和全局布局
 */
function App() {
  const handleMenuClick = (open) => {
    console.log('Mobile menu:', open ? 'opened' : 'closed');
  };

  return (
    <div className="app-container">
      <ResponsiveNavbar 
        title="职业发展计划" 
        onMenuClick={handleMenuClick}
        rightContent={
          <div className="nav-user">
            <span className="user-name">用户</span>
          </div>
        }
      />
      
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/plans" element={<div className="page-placeholder">计划页面 - 开发中</div>} />
          <Route path="/tasks" element={<div className="page-placeholder">任务页面 - 开发中</div>} />
          <Route path="/progress" element={<div className="page-placeholder">进度页面 - 开发中</div>} />
          <Route path="/profile" element={<div className="page-placeholder">个人中心 - 开发中</div>} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
