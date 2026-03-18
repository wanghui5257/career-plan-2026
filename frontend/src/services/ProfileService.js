import api from './api';

/**
 * 用户档案服务
 * 处理用户背景和目标相关的 API 调用
 */
class ProfileService {
  /**
   * 获取当前用户档案
   * @returns {Promise<Object>} 用户档案
   */
  async getMyProfile() {
    return await api.get('/profiles/me');
  }

  /**
   * 保存/更新用户档案
   * @param {Object} profileData - 档案数据
   * @returns {Promise<Object>} 保存结果
   */
  async saveMyProfile(profileData) {
    return await api.post('/profiles/me', profileData);
  }

  /**
   * 检查是否已完成档案设定
   * @returns {Promise<boolean>} 是否已设定
   */
  async hasProfile() {
    try {
      const response = await this.getMyProfile();
      return response.code === 200 && response.data !== null;
    } catch (error) {
      return false;
    }
  }
}

export default new ProfileService();
