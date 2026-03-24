import api from './api';

/**
 * 任务服务
 * 处理任务相关的 API 调用
 */
class TaskService {
  /**
   * 获取任务列表
   * @returns {Promise<Array>} 任务列表
   */
  async getTasks() {
    return await api.get('/tasks');
  }

  /**
   * 获取单个任务
   * @param {number} id - 任务 ID
   * @returns {Promise<Object>} 任务详情
   */
  async getTask(id) {
    return await api.get(\`/tasks/\${id}\`);
  }

  /**
   * 创建任务
   * @param {Object} taskData - 任务数据
   * @returns {Promise<Object>} 创建的任务
   */
  async createTask(taskData) {
    return await api.post('/tasks', taskData);
  }

  /**
   * 更新任务
   * @param {number} id - 任务 ID
   * @param {Object} taskData - 任务数据
   * @returns {Promise<Object>} 更新后的任务
   */
  async updateTask(id, taskData) {
    return await api.put(\`/tasks/\${id}\`, taskData);
  }

  /**
   * 删除任务
   * @param {number} id - 任务 ID
   * @returns {Promise<void>}
   */
  async deleteTask(id) {
    return await api.delete(\`/tasks/\${id}\`);
  }

  /**
   * 确认任务
   * @param {number} id - 任务 ID
   * @returns {Promise<Object>} 确认结果
   */
  async confirmTask(id) {
    return await api.post(\`/tasks/\${id}/confirm\`);
  }
}

export default new TaskService();
