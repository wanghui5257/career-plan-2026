import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Table, Tag, Space, Modal, Form, Input, Select, DatePicker, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, FilterOutlined } from '@ant-design/icons';
import TaskService from '../services/PlanService';
import PlanService from '../services/PlanService';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Option } = Select;

/**
 * 任务列表页面
 * 显示所有任务，支持筛选、创建、编辑、删除
 */
const TaskList = () => {
  const [loading, setLoading] = useState(false);
  const [tasks, setTasks] = useState([]);
  const [plans, setPlans] = useState([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [currentTask, setCurrentTask] = useState(null);
  const [filters, setFilters] = useState({
    status: undefined,
    priority: undefined,
    planId: undefined,
  });
  const [form] = Form.useForm();

  // 加载数据
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // 对接真实 API
      const tasksData = await TaskService.getTasks();
      const plansData = await PlanService.getPlans();
      
      setTasks(tasksData || []);
      setPlans(plansData || []);
    } catch (error) {
      console.error('加载数据失败:', error);
      message.error('加载数据失败：' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 创建任务
  const handleCreate = async (values) => {
    try {
      // 对接真实 API
      await TaskService.createTask({
        ...values,
        dueDate: values.dueDate?.format('YYYY-MM-DD'),
      });
      
      message.success('任务创建成功！');
      setCreateModalVisible(false);
      form.resetFields();
      loadData();
    } catch (error) {
      console.error('创建任务失败:', error);
      message.error('创建任务失败：' + (error.message || '未知错误'));
    }
  };

  // 编辑任务
  const handleEdit = async (values) => {
    try {
      // 对接真实 API
      await TaskService.updateTask(currentTask.id, {
        ...values,
        dueDate: values.dueDate?.format('YYYY-MM-DD'),
      });
      
      message.success('任务更新成功！');
      setEditModalVisible(false);
      setCurrentTask(null);
      form.resetFields();
      loadData();
    } catch (error) {
      console.error('更新任务失败:', error);
      message.error('更新任务失败：' + (error.message || '未知错误'));
    }
  };

  // 删除任务
  const handleDelete = async (id) => {
    try {
      // 对接真实 API
      await TaskService.deleteTask(id);
      
      message.success('任务删除成功！');
      loadData();
    } catch (error) {
      console.error('删除任务失败:', error);
      message.error('删除任务失败：' + (error.message || '未知错误'));
    }
  };

  // 打开编辑模态框
  const showEditModal = (task) => {
    setCurrentTask(task);
    form.setFieldsValue({
      title: task.title,
      description: task.description,
      planId: task.planId,
      priority: task.priority,
      status: task.status,
      dueDate: dayjs(task.dueDate),
    });
    setEditModalVisible(true);
  };

  // 状态映射
  const statusMap = {
    TODO: { text: '待办', color: 'gray' },
    IN_PROGRESS: { text: '进行中', color: 'blue' },
    DONE: { text: '已完成', color: 'green' },
  };

  // 优先级映射
  const priorityMap = {
    HIGH: { text: '高', color: 'red' },
    MEDIUM: { text: '中', color: 'orange' },
    LOW: { text: '低', color: 'green' },
  };

  // 筛选处理
  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const getFilteredTasks = () => {
    return tasks.filter(task => {
      if (filters.status && task.status !== filters.status) return false;
      if (filters.priority && task.priority !== filters.priority) return false;
      if (filters.planId && task.planId !== filters.planId) return false;
      return true;
    });
  };

  // 表格列定义
  const columns = [
    {
      title: '任务标题',
      dataIndex: 'title',
      key: 'title',
      width: 250,
    },
    {
      title: '所属计划',
      dataIndex: 'planName',
      key: 'planName',
      width: 150,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        const { text, color } = statusMap[status] || { text: status, color: 'default' };
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 80,
      render: (priority) => {
        const { text, color } = priorityMap[priority] || { text: priority, color: 'default' };
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '截止日期',
      dataIndex: 'dueDate',
      key: 'dueDate',
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => showEditModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个任务吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="link" 
              icon={<DeleteOutlined />} 
              size="small"
              danger
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const filteredTasks = getFilteredTasks();

  return (
    <div className="task-list">
      <Card
        title="✅ 任务管理"
        extra={
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => setCreateModalVisible(true)}
          >
            创建任务
          </Button>
        }
      >
        {/* 筛选区 */}
        <Space wrap style={{ marginBottom: 16 }}>
          <FilterOutlined /> <strong>筛选：</strong>
          <Select
            placeholder="全部状态"
            style={{ width: 120 }}
            allowClear
            onChange={(value) => handleFilterChange('status', value)}
          >
            <Option value="TODO">待办</Option>
            <Option value="IN_PROGRESS">进行中</Option>
            <Option value="DONE">已完成</Option>
          </Select>
          <Select
            placeholder="全部优先级"
            style={{ width: 120 }}
            allowClear
            onChange={(value) => handleFilterChange('priority', value)}
          >
            <Option value="HIGH">高</Option>
            <Option value="MEDIUM">中</Option>
            <Option value="LOW">低</Option>
          </Select>
          <Select
            placeholder="全部计划"
            style={{ width: 150 }}
            allowClear
            onChange={(value) => handleFilterChange('planId', value)}
          >
            {plans.map(plan => (
              <Option key={plan.id} value={plan.id}>{plan.name}</Option>
            ))}
          </Select>
        </Space>

        <Table
          columns={columns}
          dataSource={filteredTasks}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* 创建任务模态框 */}
      <Modal
        title="创建任务"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item 
            name="title" 
            label="任务标题" 
            rules={[{ required: true, message: '请输入任务标题' }]}
          >
            <Input placeholder="例如：学习机器学习基础" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <TextArea rows={3} placeholder="任务描述..." />
          </Form.Item>
          <Form.Item name="planId" label="所属计划">
            <Select placeholder="选择所属计划">
              {plans.map(plan => (
                <Option key={plan.id} value={plan.id}>{plan.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="priority" label="优先级" initialValue="MEDIUM">
            <Select>
              <Option value="HIGH">高</Option>
              <Option value="MEDIUM">中</Option>
              <Option value="LOW">低</Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue="TODO">
            <Select>
              <Option value="TODO">待办</Option>
              <Option value="IN_PROGRESS">进行中</Option>
              <Option value="DONE">已完成</Option>
            </Select>
          </Form.Item>
          <Form.Item name="dueDate" label="截止日期">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              创建
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑任务模态框 */}
      <Modal
        title="编辑任务"
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          setCurrentTask(null);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleEdit}>
          <Form.Item 
            name="title" 
            label="任务标题" 
            rules={[{ required: true, message: '请输入任务标题' }]}
          >
            <Input placeholder="例如：学习机器学习基础" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <TextArea rows={3} placeholder="任务描述..." />
          </Form.Item>
          <Form.Item name="planId" label="所属计划">
            <Select placeholder="选择所属计划">
              {plans.map(plan => (
                <Option key={plan.id} value={plan.id}>{plan.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="priority" label="优先级">
            <Select>
              <Option value="HIGH">高</Option>
              <Option value="MEDIUM">中</Option>
              <Option value="LOW">低</Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select>
              <Option value="TODO">待办</Option>
              <Option value="IN_PROGRESS">进行中</Option>
              <Option value="DONE">已完成</Option>
            </Select>
          </Form.Item>
          <Form.Item name="dueDate" label="截止日期">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default TaskList;
