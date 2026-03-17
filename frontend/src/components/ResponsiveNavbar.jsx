import React, { useState } from 'react';
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

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
    if (onMenuClick) {
      onMenuClick(!mobileMenuOpen);
    }
  };

  return (
    <div className="responsive-navbar">
      {/* 桌面端导航 */}
      <nav className="desktop-nav">
        <div className="nav-content">
          <span className="nav-title">{title}</span>
          <div className="nav-menu">
            <a href="#dashboard" className="nav-item">首页</a>
            <a href="#plans" className="nav-item">计划</a>
            <a href="#tasks" className="nav-item">任务</a>
            <a href="#progress" className="nav-item">进度</a>
            <a href="#profile" className="nav-item">个人中心</a>
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
          <div className="mobile-menu-item" onClick={() => { window.location.hash = '#dashboard'; setMobileMenuOpen(false); }}>
            🏠 首页
          </div>
          <div className="mobile-menu-item" onClick={() => { window.location.hash = '#plans'; setMobileMenuOpen(false); }}>
            📋 计划
          </div>
          <div className="mobile-menu-item" onClick={() => { window.location.hash = '#tasks'; setMobileMenuOpen(false); }}>
            ✅ 任务
          </div>
          <div className="mobile-menu-item" onClick={() => { window.location.hash = '#progress'; setMobileMenuOpen(false); }}>
            📊 进度
          </div>
          <div className="mobile-menu-item" onClick={() => { window.location.hash = '#profile'; setMobileMenuOpen(false); }}>
            👤 个人中心
          </div>
        </div>
      )}
    </div>
  );
};

export default ResponsiveNavbar;
