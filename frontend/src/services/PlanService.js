import api from './api';

/**
 * 计划服务
 * 处理计划相关的 API 调用
 */
class PlanService {
  /**
   * 获取计划列表
   * @returns {Promise<Array>} 计划列表
   */
  async getPlans() {
    return await api.get('/plans');
  }

  /**
   * 获取单个计划
   * @param {number} id - 计划 ID
   * @returns {Promise<Object>} 计划详情
   */
  async getPlan(id) {
    return await api.get(`/plans/${id}`);
  }

  /**
   * 创建计划
   * @param {Object} planData - 计划数据
   * @returns {Promise<Object>} 创建的计划
   */
  async createPlan(planData) {
    return await api.post('/plans', planData);
  }

  /**
   * 更新计划
   * @param {number} id - 计划 ID
   * @param {Object} planData - 计划数据
   * @returns {Promise<Object>} 更新后的计划
   */
  async updatePlan(id, planData) {
    return await api.put(`/plans/${id}`, planData);
  }

  /**
   * 删除计划
   * @param {number} id - 计划 ID
   * @returns {Promise<void>}
   */
  async deletePlan(id) {
    return await api.delete(`/plans/${id}`);
  }

  /**
   * 获取计划的任务列表
   * @param {number} planId - 计划 ID
   * @returns {Promise<Array>} 任务列表
   */
  async getPlanTasks(planId) {
    return await api.get(`/plans/${planId}/tasks`);
  }

  /**
   * 添加任务到计划
   * @param {number} planId - 计划 ID
   * @param {Object} taskData - 任务数据
   * @returns {Promise<Object>} 创建的任务
   */
  async addTaskToPlan(planId, taskData) {
    return await api.post(`/plans/${planId}/tasks`, taskData);
  }
}

export default new PlanService();
