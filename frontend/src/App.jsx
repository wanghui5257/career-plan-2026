import React from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import ResponsiveNavbar from './components/ResponsiveNavbar';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import UserProfileWizard from './pages/UserProfileWizard';
import UserProfile from './pages/UserProfile';
import PlanList from './pages/PlanList';
import TaskList from './pages/TaskList';
import ProgressHistory from './pages/ProgressHistory';
import ChangePasswordForm from './components/ChangePasswordForm';
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
  const navigate = useNavigate();
  
  const handleMenuClick = (open) => {
    console.log('Mobile menu:', open ? 'opened' : 'closed');
  };

  const handleLogout = async () => {
    await AuthService.logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className="app-container">
      <Routes>
        {/* 登录页（无需认证） */}
        <Route path="/login" element={<Login />} />
        
        {/* 用户档案向导（无需认证，但需要登录） */}
        <Route
          path="/profile/wizard"
          element={
            <PrivateRoute>
              <UserProfileWizard />
            </PrivateRoute>
          }
        />
        
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
        
        {/* 计划管理页面 */}
        <Route
          path="/plans"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="计划管理" 
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
                  <PlanList />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 任务管理页面 */}
        <Route
          path="/tasks"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="任务管理" 
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
                  <TaskList />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 进度历史页面 */}
        <Route
          path="/progress"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="进度历史" 
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
                  <ProgressHistory />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        {/* 个人中心 - 用户资料编辑 */}
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
          path="/settings"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar 
                  title="账号设置" 
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
