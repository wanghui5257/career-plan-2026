import api from './api';

/**
 * 认证服务
 * 处理用户登录、登出、Token 管理
 */
class AuthService {
  /**
   * 用户登录
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @returns {Promise} 登录结果（包含 token 和用户信息）
   */
  async login(username, password) {
    try {
      const response = await api.post('/auth/login', {
        username,
        password,
      });
      
      // 保存 Token 和用户信息
      if (response.token) {
        localStorage.setItem('jwt_token', response.token);
      }
      if (response.user) {
        localStorage.setItem('user_info', JSON.stringify(response.user));
      }
      
      return response;
    } catch (error) {
      console.error('登录失败:', error);
      throw error;
    }
  }

  /**
   * 用户登出
   */
  async logout() {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('登出失败:', error);
    } finally {
      // 清除本地存储
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
    }
  }

  /**
   * 获取当前登录用户信息
   * @returns {Object|null} 用户信息
   */
  getCurrentUser() {
    const userInfo = localStorage.getItem('user_info');
    return userInfo ? JSON.parse(userInfo) : null;
  }

  /**
   * 检查是否已登录
   * @returns {boolean} 是否已登录
   */
  isAuthenticated() {
    return !!localStorage.getItem('jwt_token');
  }

  /**
   * 获取 Token
   * @returns {string|null} JWT Token
   */
  getToken() {
    return localStorage.getItem('jwt_token');
  }
}

export default new AuthService();
