import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ResponsiveNavbar from './components/ResponsiveNavbar';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Profile from './pages/Profile';
import ChangePasswordForm from './pages/ChangePasswordForm';
import PrivateRoute from './components/PrivateRoute';
import AuthService from './services/AuthService';
import './styles/responsive.css';
import './styles/mobile.css';
import 'antd/dist/reset.css';

/**
 * 主应用组件
 * 配置路由和全局布局
 */
function App() {
  const handleMenuClick = (open) => {
    console.log('Mobile menu:', open ? 'opened' : 'closed');
  };

  const handleLogout = async () => {
    await AuthService.logout();
    window.location.href = '/login';
  };

  return (
    <div className="app-container">
      <Routes>
        {/* 登录页（无需认证） */}
        <Route path="/login" element={<Login />} />
        
        {/* 需要认证的路由 */}
        <Route
          path="/"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="职业发展计划" 
                  onMenuClick={handleMenuClick}
                  rightContent={
                    <div className="nav-user">
                      <span className="user-name">
                        {AuthService.getCurrentUser()?.username || '用户'}
                      </span>
                      <button className="logout-btn" onClick={handleLogout}>
                        退出
                      </button>
                    </div>
                  }
                />
                <main className="main-content">
                  <Dashboard />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="职业发展计划" 
                  onMenuClick={handleMenuClick}
                  rightContent={
                    <div className="nav-user">
                      <span className="user-name">
                        {AuthService.getCurrentUser()?.username || '用户'}
                      </span>
                      <button className="logout-btn" onClick={handleLogout}>
                        退出
                      </button>
                    </div>
                  }
                />
                <main className="main-content">
                  <Dashboard />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 占位页面 */}
        <Route
          path="/plans"
          element={
            <PrivateRoute>
              <div className="page-placeholder">计划页面 - 开发中</div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/tasks"
          element={
            <PrivateRoute>
              <div className="page-placeholder">任务页面 - 开发中</div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/progress"
          element={
            <PrivateRoute>
              <div className="page-placeholder">进度页面 - 开发中</div>
            </PrivateRoute>
          }
        />
        
        {/* 个人中心 */}
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="个人中心" 
                  onMenuClick={handleMenuClick}
                  rightContent={
                    <div className="nav-user">
                      <span className="user-name">
                        {AuthService.getCurrentUser()?.username || '用户'}
                      </span>
                      <button className="logout-btn" onClick={handleLogout}>
                        退出
                      </button>
                    </div>
                  }
                />
                <main className="main-content">
                  <UserProfile />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 修改密码 */}
        <Route
          path="/profile/change-password"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="修改密码" 
                  onMenuClick={handleMenuClick}
                  rightContent={
                    <div className="nav-user">
                      <span className="user-name">
                        {AuthService.getCurrentUser()?.username || '用户'}
                      </span>
                      <button className="logout-btn" onClick={handleLogout}>
                        退出
                      </button>
                    </div>
                  }
                />
                <main className="main-content">
                  <ChangePasswordForm />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 默认重定向 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default App;
