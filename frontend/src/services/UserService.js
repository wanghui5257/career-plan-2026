import api from './api';

class UserService {
  async getProfile() {
    return await api.get('/user/profile');
  }

  async updateProfile(data) {
    return await api.put('/user/profile', data);
  }

  async changePassword(oldPassword, newPassword) {
    return await api.put('/user/password', { oldPassword, newPassword });
  }
}

export default new UserService();
