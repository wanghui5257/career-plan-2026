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
   * @param {number} planId - 计划 ID（可选，不传则获取所有计划的进度历史）
   * @returns {Promise<Array>} 进度历史记录
   */
  async getProgressHistory(planId) {
    // 参数验证：如果 planId 为空、null、undefined 或字符串 'undefined'，则获取所有计划的进度历史
    if (!planId || planId === 'undefined' || planId === null) {
      console.warn('getProgressHistory: planId 为空，获取所有计划的进度历史', planId);
      return await api.get('/progress/history');
    }
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

  /**
   * 记录进度
   * @param {Object} progressData - 进度数据 { planId, taskId, progressPercentage, notes }
   * @returns {Promise<Object>} 创建的进度记录
   */
  async createProgress(progressData) {
    // 参数验证
    if (!progressData || !progressData.planId) {
      console.warn('createProgress: planId is required', progressData);
      throw new Error('计划 ID 不能为空');
    }
    return await api.post('/progress', progressData);
  }

  /**
   * 更新进度记录
   * @param {number} id - 进度记录 ID
   * @param {Object} progressData - 进度数据
   * @returns {Promise<Object>} 更新后的进度记录
   */
  async updateProgressRecord(id, progressData) {
    if (!id || id === 'undefined' || id === null) {
      console.warn('updateProgressRecord: invalid id', id);
      throw new Error('进度记录 ID 不能为空');
    }
    return await api.put(`/progress/${id}`, progressData);
  }

  /**
   * 删除进度记录
   * @param {number} id - 进度记录 ID
   * @returns {Promise<void>}
   */
  async deleteProgress(id) {
    if (!id || id === 'undefined' || id === null) {
      console.warn('deleteProgress: invalid id', id);
      throw new Error('进度记录 ID 不能为空');
    }
    return await api.delete(`/progress/${id}`);
  }
}

export default new ProgressService();
