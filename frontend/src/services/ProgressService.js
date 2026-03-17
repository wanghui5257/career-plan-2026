import api from './api';

/**
 * 进度服务
 * 处理进度相关的 API 调用
 */
class ProgressService {
  /**
   * 获取计划进度
   * @param {number} planId - 计划 ID
   * @returns {Promise<Object>} 进度数据
   */
  async getPlanProgress(planId) {
    return await api.get(`/progress/plan/${planId}`);
  }

  /**
   * 获取进度摘要
   * @returns {Promise<Object>} 进度摘要
   */
  async getProgressSummary() {
    return await api.get('/progress/summary');
  }

  /**
   * 获取进度历史
   * @param {number} planId - 计划 ID
   * @returns {Promise<Array>} 进度历史记录
   */
  async getProgressHistory(planId) {
    return await api.get(`/progress/history/${planId}`);
  }

  /**
   * 创建进度报告
   * @param {Object} progressData - 进度数据
   * @returns {Promise<Object>} 创建的进度报告
   */
  async createProgress(progressData) {
    return await api.post('/progress-reports', progressData);
  }

  /**
   * 更新进度报告
   * @param {number} id - 进度报告 ID
   * @param {Object} progressData - 进度数据
   * @returns {Promise<Object>} 更新后的进度报告
   */
  async updateProgress(id, progressData) {
    return await api.put(`/progress-reports/${id}`, progressData);
  }
}

export default new ProgressService();
