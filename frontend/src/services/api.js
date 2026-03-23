import axios from 'axios';

// API 基础配置（支持环境变量）
// 优先级：.env.production > .env.staging > 默认值
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
                     'https://plan.shujuyunxiang.com/back-server/api/v1';

// 创建 Axios 实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 自动附加 JWT Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    // 401 - 未授权，跳转到登录页
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
    }
    
    // 统一错误处理
    const errorMessage = error.response?.data?.message || '请求失败，请稍后重试';
    console.error('API Error:', errorMessage);
    
    return Promise.reject({
      message: errorMessage,
      status: error.response?.status,
    });
  }
);

export default api;
