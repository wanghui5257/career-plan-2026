import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ResponsiveNavbar from './components/ResponsiveNavbar';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import PlanList from './pages/PlanList';
import TaskList from './pages/TaskList';
import ProgressHistory from './pages/ProgressHistory';
import Profile from './pages/Profile';
import PrivateRoute from './components/PrivateRoute';
import AuthService from './services/AuthService';
import './styles/responsive.css';
import './styles/mobile.css';
import 'antd/dist/reset.css';

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
        <Route path="/login" element={<Login />} />
        
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
          path="/plans"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar title="职业发展计划" rightContent={<span></span>} />
                <main className="main-content">
                  <PlanList />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/tasks"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar title="职业发展计划" rightContent={<span></span>} />
                <main className="main-content">
                  <TaskList />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/progress"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar title="职业发展计划" rightContent={<span></span>} />
                <main className="main-content">
                  <ProgressHistory />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <div className="app-with-navbar">
                <ResponsiveNavbar title="职业发展计划" rightContent={<span></span>} />
                <main className="main-content">
                  <Profile />
                </main>
              </div>
            </PrivateRoute>
          }
        />
        
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default App;
