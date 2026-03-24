import api from './api';

class UserService {
  async getProfile() {
    // api.js 拦截器已返回 response.data = {code, message, data: {...}}
    const response = await api.get('/user/profile');
    // 直接返回 data 字段（用户数据）
    if (response && response.data) {
      return response.data;
    }
    // 兼容：如果 response 本身就是用户数据
    return response;
  }

  async updateProfile(data) {
    const response = await api.put('/user/profile', data);
    if (response && response.data) {
      return response.data;
    }
    return response;
  }

  async changePassword(oldPassword, newPassword) {
    return await api.put('/user/password', { oldPassword, newPassword });
  }
}

export default new UserService();
