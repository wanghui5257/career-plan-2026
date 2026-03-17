import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import AuthService from '../services/AuthService';

/**
 * 私有路由组件
 * 未登录用户重定向到登录页
 */
const PrivateRoute = ({ children }) => {
  const isAuthenticated = AuthService.isAuthenticated();
  const location = useLocation();

  if (!isAuthenticated) {
    // 重定向到登录页，保存当前路径
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

export default PrivateRoute;
