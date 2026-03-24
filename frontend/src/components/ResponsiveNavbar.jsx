import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './ResponsiveNavbar.css';

/**
 * 响应式导航栏组件
 */
const ResponsiveNavbar = ({ 
  title = '职业发展计划',
  onMenuClick,
  rightContent = null
}) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
    if (onMenuClick) {
      onMenuClick(!mobileMenuOpen);
    }
  };

  const handleNavClick = (path) => {
    navigate(path);
    setMobileMenuOpen(false);
  };

  return (
    <div className="responsive-navbar">
      {/* 桌面端导航 */}
      <nav className="desktop-nav">
        <div className="nav-content">
          <span className="nav-title">{title}</span>
          <div className="nav-menu">
            <Link to="/" className="nav-item">首页</Link>
            <Link to="/plans" className="nav-item">计划</Link>
            <Link to="/tasks" className="nav-item">任务</Link>
            <Link to="/progress" className="nav-item">进度</Link>
            <Link to="/profile" className="nav-item">个人中心</Link>
          </div>
          {rightContent && <div className="nav-right">{rightContent}</div>}
        </div>
      </nav>

      {/* 移动端导航 */}
      <nav className="mobile-nav">
        <span className="nav-title">{title}</span>
        <button className="hamburger-btn" onClick={toggleMobileMenu}>
          <span className={`hamburger-icon ${mobileMenuOpen ? 'open' : ''}`}>
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
      </nav>

      {/* 移动端下拉菜单 */}
      {mobileMenuOpen && (
        <div className="mobile-menu">
          <div className="mobile-menu-item" onClick={() => handleNavClick('/')}>
            🏠 首页
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavClick('/plans')}>
            📋 计划
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavClick('/tasks')}>
            ✅ 任务
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavClick('/progress')}>
            📊 进度
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavClick('/profile')}>
            👤 个人中心
          </div>
        </div>
      )}
    </div>
  );
};

export default ResponsiveNavbar;
