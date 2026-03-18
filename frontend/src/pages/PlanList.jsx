import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Table, Tag, Space, Modal, Form, Input, DatePicker, message, Progress, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import PlanService from '../services/PlanService';
import dayjs from 'dayjs';

const { TextArea } = Input;

/**
 * 计划列表页面
 * 显示所有计划，支持创建、查看、编辑、删除
 */
const PlanList = () => {
  const [loading, setLoading] = useState(false);
  const [plans, setPlans] = useState([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [currentPlan, setCurrentPlan] = useState(null);
  const [form] = Form.useForm();

  // 加载计划列表
  useEffect(() => {
    loadPlans();
  }, []);

  const loadPlans = async () => {
    setLoading(true);
    try {
      // 对接真实 API
      const data = await PlanService.getPlans();
      setPlans(data || []);
    } catch (error) {
      console.error('加载计划失败:', error);
      message.error('加载计划失败：' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 创建计划
  const handleCreate = async (values) => {
    try {
      // 对接真实 API
      await PlanService.createPlan({
        ...values,
        startDate: values.startDate?.format('YYYY-MM-DD'),
        endDate: values.endDate?.format('YYYY-MM-DD'),
      });
      
      message.success('计划创建成功！');
      setCreateModalVisible(false);
      form.resetFields();
      loadPlans();
    } catch (error) {
      console.error('创建计划失败:', error);
      message.error('创建计划失败：' + (error.message || '未知错误'));
    }
  };

  // 编辑计划
  const handleEdit = async (values) => {
    try {
      // 对接真实 API
      await PlanService.updatePlan(currentPlan.id, {
        ...values,
        startDate: values.startDate?.format('YYYY-MM-DD'),
        endDate: values.endDate?.format('YYYY-MM-DD'),
      });
      
      message.success('计划更新成功！');
      setEditModalVisible(false);
      setCurrentPlan(null);
      form.resetFields();
      loadPlans();
    } catch (error) {
      console.error('更新计划失败:', error);
      message.error('更新计划失败：' + (error.message || '未知错误'));
    }
  };

  // 删除计划
  const handleDelete = async (id) => {
    try {
      // 对接真实 API
      await PlanService.deletePlan(id);
      
      message.success('计划删除成功！');
      loadPlans();
    } catch (error) {
      console.error('删除计划失败:', error);
      message.error('删除计划失败：' + (error.message || '未知错误'));
    }
  };

  // 打开编辑模态框
  const showEditModal = (plan) => {
    setCurrentPlan(plan);
    form.setFieldsValue({
      name: plan.name,
      description: plan.description,
      startDate: dayjs(plan.startDate),
      endDate: dayjs(plan.endDate),
    });
    setEditModalVisible(true);
  };

  // 状态映射
  const statusMap = {
    NOT_STARTED: { text: '未开始', color: 'gray' },
    IN_PROGRESS: { text: '进行中', color: 'blue' },
    COMPLETED: { text: '已完成', color: 'green' },
  };

  // 表格列定义
  const columns = [
    {
      title: '计划名称',
      dataIndex: 'name',
      key: 'name',
      width: 250,
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
      title: '进度',
      key: 'progress',
      width: 200,
      render: (_, record) => (
        <Progress 
          percent={record.progress || 0} 
          status={record.progress === 100 ? 'success' : 'active'}
          format={() => `${record.completedTasks || 0}/${record.totalTasks || 0}`}
        />
      ),
    },
    {
      title: '开始日期',
      dataIndex: 'startDate',
      key: 'startDate',
      width: 120,
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      key: 'endDate',
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
            icon={<EyeOutlined />} 
            size="small"
            onClick={() => message.info('查看详情功能开发中')}
          >
            查看
          </Button>
          <Button 
            type="link" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => showEditModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个计划吗？"
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

  return (
    <div className="plan-list">
      <Card
        title="📋 计划管理"
        extra={
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={() => setCreateModalVisible(true)}
          >
            创建计划
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={plans}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* 创建计划模态框 */}
      <Modal
        title="创建计划"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item 
            name="name" 
            label="计划名称" 
            rules={[{ required: true, message: '请输入计划名称' }]}
          >
            <Input placeholder="例如：AI 学习计划" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <TextArea rows={3} placeholder="计划描述..." />
          </Form.Item>
          <Form.Item name="startDate" label="开始日期">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="endDate" label="结束日期">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              创建
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑计划模态框 */}
      <Modal
        title="编辑计划"
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          setCurrentPlan(null);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleEdit}>
          <Form.Item 
            name="name" 
            label="计划名称" 
            rules={[{ required: true, message: '请输入计划名称' }]}
          >
            <Input placeholder="例如：AI 学习计划" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <TextArea rows={3} placeholder="计划描述..." />
          </Form.Item>
          <Form.Item name="startDate" label="开始日期">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="endDate" label="结束日期">
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

export default PlanList;
