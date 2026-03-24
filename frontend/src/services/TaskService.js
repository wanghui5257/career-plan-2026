import api from './api';

class TaskService {
  async getTasks() {
    return await api.get('/tasks');
  }

  async getTask(id) {
    return await api.get('/tasks/' + id);
  }

  async createTask(taskData) {
    return await api.post('/tasks', taskData);
  }

  async updateTask(id, taskData) {
    return await api.put('/tasks/' + id, taskData);
  }

  async deleteTask(id) {
    return await api.delete('/tasks/' + id);
  }

  async confirmTask(id) {
    return await api.post('/tasks/' + id + '/confirm');
  }
}

export default new TaskService();
