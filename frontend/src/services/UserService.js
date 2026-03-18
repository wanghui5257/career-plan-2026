import api from './api';

/**
 * 用户服务
 * 处理用户相关的 API 调用（密码修改、资料更新等）
 */
class UserService {
  /**
   * 修改密码
   * @param {string} oldPassword - 旧密码
   * @param {string} newPassword - 新密码
   * @returns {Promise<Object>} 修改结果
   */
  async changePassword(oldPassword, newPassword) {
    return await api.put('/user/password', {
      oldPassword,
      newPassword,
    });
  }

  /**
   * 获取当前用户资料
   * @returns {Promise<Object>} 用户资料
   */
  async getMyProfile() {
    return await api.get('/user/profile');
  }

  /**
   * 更新当前用户资料
   * @param {Object} profileData - 资料数据
   * @returns {Promise<Object>} 更新结果
   */
  async updateMyProfile(profileData) {
    return await api.put('/user/profile', profileData);
  }

  /**
   * 获取用户资料（顾问专用）
   * @param {number} userId - 用户 ID
   * @returns {Promise<Object>} 用户资料
   */
  async getUserProfile(userId) {
    return await api.get(`/user/${userId}/profile`);
  }

  /**
   * 确认任务
   * @param {number} taskId - 任务 ID
   * @param {string} comment - 可选评论
   * @returns {Promise<Object>} 确认结果
   */
  async confirmTask(taskId, comment = null) {
    const body = comment ? { comment } : {};
    return await api.post(`/tasks/${taskId}/confirm`, body);
  }
}

export default new UserService();
