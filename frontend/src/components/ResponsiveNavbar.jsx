import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ResponsiveNavbar.css';

/**
 * 响应式导航栏组件
 * - 桌面端：显示完整导航菜单
 * - 移动端：显示汉堡菜单
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

  const handleNavigation = (path) => {
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
            <span className="nav-item" onClick={() => handleNavigation('/')}>首页</span>
            <span className="nav-item" onClick={() => handleNavigation('/plans')}>计划</span>
            <span className="nav-item" onClick={() => handleNavigation('/tasks')}>任务</span>
            <span className="nav-item" onClick={() => handleNavigation('/progress')}>进度</span>
            <span className="nav-item" onClick={() => handleNavigation('/profile')}>个人中心</span>
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
          <div className="mobile-menu-item" onClick={() => handleNavigation('/')}>
            🏠 首页
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavigation('/plans')}>
            📋 计划
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavigation('/tasks')}>
            ✅ 任务
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavigation('/progress')}>
            📊 进度
          </div>
          <div className="mobile-menu-item" onClick={() => handleNavigation('/profile')}>
            👤 个人中心
          </div>
        </div>
      )}
    </div>
  );
};

export default ResponsiveNavbar;
