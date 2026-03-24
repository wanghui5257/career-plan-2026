import axios from 'axios';

// API 基础配置 - 根据域名自动选择正确的后端
const getApiBaseUrl = () => {
  const hostname = window.location.hostname;
  
  // Production 环境
  if (hostname === 'plan.shujuyunxiang.com') {
    return 'https://plan.shujuyunxiang.com/back-server/api/v1';
  }
  
  // Staging 环境（默认）
  return 'https://staging.plan.shujuyunxiang.com/back-server/api/v1';
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || getApiBaseUrl();

console.log('API Base URL:', API_BASE_URL);

// 创建 Axios 实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
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

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
    }
    
    const errorMessage = error.response?.data?.message || '请求失败，请稍后重试';
    console.error('API Error:', errorMessage);
    
    return Promise.reject({
      message: errorMessage,
      status: error.response?.status,
    });
  }
);

export default api;
