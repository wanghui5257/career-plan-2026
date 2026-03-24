import api from './api';

class UserService {
  async getProfile() {
    const response = await api.get('/user/profile');
    // 后端返回格式: { code: 200, message: "...", data: {...} }
    return response.data || response;
  }

  async updateProfile(data) {
    const response = await api.put('/user/profile', data);
    return response.data || response;
  }

  async changePassword(oldPassword, newPassword) {
    return await api.put('/user/password', { oldPassword, newPassword });
  }
}

export default new UserService();
