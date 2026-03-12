import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { message } from 'antd'

// API 基础配置 - 生产环境 HTTPS
const API_BASE_URL = 'https://plan.shujuyunxiang.com/api/v1'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10 秒超时
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 直接返回响应数据
    return response
  },
  (error: AxiosError) => {
    // 统一错误处理
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 400:
          message.error('请求参数错误')
          break
        case 401:
          message.error('未授权，请重新登录')
          // 可以在这里清除 token 并跳转到登录页
          localStorage.removeItem('token')
          break
        case 403:
          message.error('拒绝访问')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器错误')
          break
        default:
          message.error((error.response.data as any)?.message || '请求失败')
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      message.error('网络错误，请检查网络连接')
    } else {
      // 其他错误
      message.error(error.message || '未知错误')
    }
    
    return Promise.reject(error)
  }
)

// 导出请求方法
export default request

// 便捷方法
export const api = {
  // GET 请求
  get: <T = any>(url: string, config?: AxiosRequestConfig) => {
    return request.get<T, AxiosResponse<T>>(url, config)
  },

  // POST 请求
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => {
    return request.post<T, AxiosResponse<T>>(url, data, config)
  },

  // PUT 请求
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) => {
    return request.put<T, AxiosResponse<T>>(url, data, config)
  },

  // DELETE 请求
  delete: <T = any>(url: string, config?: AxiosRequestConfig) => {
    return request.delete<T, AxiosResponse<T>>(url, config)
  },

  // 任务相关 API
  tasks: {
    // 获取任务列表
    list: (params?: any) => request.get<Task[]>('/tasks', { params }),
    
    // 获取单个任务
    get: (id: string) => request.get<Task>(`/tasks/${id}`),
    
    // 创建任务
    create: (data: any) => request.post<Task>('/tasks', data),
    
    // 更新任务
    update: (id: string, data: any) => request.put<Task>(`/tasks/${id}`, data),
    
    // 删除任务
    delete: (id: string) => request.delete(`/tasks/${id}`),
  },

  // 用户相关 API
  users: {
    // 获取当前用户信息
    me: () => request.get<User>('/users/me'),
    
    // 登录
    login: (credentials: { username: string; password: string }) =>
      request.post<{ token: string; user: User }>('/auth/login', credentials),
    
    // 登出
    logout: () => request.post('/auth/logout'),
  },
}

// 类型定义
export interface Task {
  id: string
  title: string
  description: string
  status: 'todo' | 'in_progress' | 'completed' | 'blocked'
  priority: 'low' | 'medium' | 'high' | 'urgent'
  assignee: string
  startDate?: string
  dueDate?: string
  progress: number
  tags: string[]
  createdAt: string
  updatedAt: string
}

export interface User {
  id: string
  name: string
  email: string
  avatar?: string
  role: string
}

// Loading 状态管理
let loadingCount = 0

export const showLoading = () => {
  loadingCount++
  // 可以在这里显示全局 loading
}

export const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    // 可以在这里隐藏全局 loading
  }
}

// 带 loading 的请求包装器
export const requestWithLoading = async <T>(promise: Promise<T>): Promise<T> => {
  showLoading()
  try {
    return await promise
  } finally {
    hideLoading()
  }
}
