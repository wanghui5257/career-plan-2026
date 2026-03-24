import React, { useState, useEffect } from 'react';
import { Card, Button, Timeline, Modal, Form, Input, Slider, message, Table, Tag, Space, Popconfirm, DatePicker, Row, Col } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, LineChartOutlined } from '@ant-design/icons';
import ProgressService from '../services/ProgressService';
import PlanService from '../services/PlanService';
import dayjs from 'dayjs';

const { TextArea } = Input;

/**
 * 进度历史页面
 * 显示进度记录时间线，支持添加、编辑、删除进度
 */
const ProgressHistory = () => {
  const [loading, setLoading] = useState(false);
  const [progressRecords, setProgressRecords] = useState([]);
  const [plans, setPlans] = useState([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [selectedPlanId, setSelectedPlanId] = useState(null);
  const [form] = Form.useForm();

  // 加载数据
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // 对接真实 API
      // const plansData = await PlanService.getPlans();
      // const historyData = await ProgressService.getProgressHistory(selectedPlanId);
      
      // TODO: 暂时使用 Mock 数据，等待 Progress API 完成
      const mockPlans = [
        { id: 1, name: 'AI 学习计划', progress: 45 },
        { id: 2, name: '前端进阶计划', progress: 60 },
        { id: 3, name: '英语提升计划', progress: 0 }
      ];
      
      const mockHistory = [
        {
          id: 1,
          planId: 1,
          planName: 'AI 学习计划',
          progress: 20,
          notes: '完成机器学习基础学习',
          createdAt: '2026-03-01 10:00:00',
          createdBy: 'admin'
        },
        {
          id: 2,
          planId: 1,
          planName: 'AI 学习计划',
          progress: 35,
          notes: '开始深度学习课程',
          createdAt: '2026-03-10 14:30:00',
          createdBy: 'admin'
        },
        {
          id: 3,
          planId: 1,
          planName: 'AI 学习计划',
          progress: 45,
          notes: '完成第一个实战项目',
          createdAt: '2026-03-15 16:00:00',
          createdBy: 'admin'
        },
        {
          id: 4,
          planId: 2,
          planName: '前端进阶计划',
          progress: 60,
          notes: 'React Hooks 学习完成',
          createdAt: '2026-03-12 09:00:00',
          createdBy: 'admin'
        }
      ];
      
      setPlans(mockPlans);
      setProgressRecords(mockHistory);
    } catch (error) {
      console.error('加载数据失败:', error);
      message.error('加载数据失败：' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 创建进度记录
  const handleCreate = async (values) => {
    try {
      // 对接真实 API
      await ProgressService.createProgress({
        ...values,
        planId: selectedPlanId,
      });
      
      message.success('进度记录成功！');
      setCreateModalVisible(false);
      form.resetFields();
      loadData();
    } catch (error) {
      console.error('创建进度记录失败:', error);
      message.error('创建进度记录失败：' + (error.message || '未知错误'));
    }
  };

  // 编辑进度记录
  const handleEdit = async (values) => {
    try {
      // 对接真实 API
      await ProgressService.updateProgressRecord(currentRecord.id, values);
      
      message.success('进度更新成功！');
      setEditModalVisible(false);
      setCurrentRecord(null);
      form.resetFields();
      loadData();
    } catch (error) {
      console.error('更新进度记录失败:', error);
      message.error('更新进度记录失败：' + (error.message || '未知错误'));
    }
  };

  // 删除进度记录
  const handleDelete = async (id) => {
    try {
      // 对接真实 API
      await ProgressService.deleteProgress(id);
      
      message.success('进度记录删除成功！');
      loadData();
    } catch (error) {
      console.error('删除进度记录失败:', error);
      message.error('删除进度记录失败：' + (error.message || '未知错误'));
    }
  };

  // 打开编辑模态框
  const showEditModal = (record) => {
    setCurrentRecord(record);
    form.setFieldsValue({
      progress: record.progress,
      notes: record.notes,
    });
    setEditModalVisible(true);
  };

  // 按计划筛选
  const getFilteredRecords = () => {
    if (!selectedPlanId) return progressRecords;
    return progressRecords.filter(record => record.planId === selectedPlanId);
  };

  // 时间线颜色
  const getTimelineColor = (progress) => {
    if (progress >= 80) return 'green';
    if (progress >= 50) return 'blue';
    if (progress >= 20) return 'orange';
    return 'gray';
  };

  // 表格列定义
  const columns = [
    {
      title: '计划名称',
      dataIndex: 'planName',
      key: 'planName',
      width: 200,
    },
    {
      title: '进度',
      dataIndex: 'progress',
      key: 'progress',
      width: 150,
      render: (progress) => (
        <Tag color={getTimelineColor(progress)}>{progress}%</Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'notes',
      key: 'notes',
      ellipsis: true,
    },
    {
      title: '记录时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
    },
    {
      title: '记录人',
      dataIndex: 'createdBy',
      key: 'createdBy',
      width: 100,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
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
            title="确定要删除这条进度记录吗？"
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

  const filteredRecords = getFilteredRecords();

  return (
    <div className="progress-history">
      <Row gutter={[16, 16]}>
        {/* 左侧：计划选择 + 进度记录 */}
        <Col xs={24} lg={16}>
          <Card
            title="📈 进度历史"
            extra={
              <Space>
                <Button 
                  icon={<PlusOutlined />} 
                  onClick={() => setCreateModalVisible(true)}
                  disabled={!selectedPlanId}
                >
                  记录进度
                </Button>
              </Space>
            }
          >
            {/* 计划筛选 */}
            <Space wrap style={{ marginBottom: 16 }}>
              <strong>选择计划：</strong>
              <Space wrap>
                <Button 
                  type={!selectedPlanId ? 'primary' : 'default'}
                  onClick={() => setSelectedPlanId(null)}
                >
                  全部
                </Button>
                {plans.map(plan => (
                  <Button
                    key={plan.id}
                    type={selectedPlanId === plan.id ? 'primary' : 'default'}
                    onClick={() => setSelectedPlanId(plan.id)}
                  >
                    {plan.name}
                  </Button>
                ))}
              </Space>
            </Space>

            {/* 进度记录表格 */}
            <Table
              columns={columns}
              dataSource={filteredRecords}
              rowKey="id"
              loading={loading}
              pagination={{ pageSize: 10 }}
            />
          </Card>
        </Col>

        {/* 右侧：时间线可视化 */}
        <Col xs={24} lg={8}>
          <Card
            title="📊 进度趋势"
            extra={<LineChartOutlined />}
          >
            {filteredRecords.length > 0 ? (
              <Timeline
                items={filteredRecords
                  .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                  .map(record => ({
                    key: record.id,
                    color: getTimelineColor(record.progress),
                    children: (
                      <div>
                        <div style={{ fontWeight: 'bold' }}>
                          {record.planName} - {record.progress}%
                        </div>
                        <div style={{ fontSize: '12px', color: '#666' }}>
                          {record.notes}
                        </div>
                        <div style={{ fontSize: '12px', color: '#999' }}>
                          {record.createdAt}
                        </div>
                      </div>
                    ),
                  }))}
              />
            ) : (
              <div style={{ textAlign: 'center', color: '#999', padding: '40px 0' }}>
                暂无进度记录
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 创建进度记录模态框 */}
      <Modal
        title="记录进度"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        footer={null}
        width={500}
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item 
            name="progress" 
            label="进度百分比" 
            rules={[{ required: true, message: '请输入进度' }]}
          >
            <Slider marks={{ 0: '0%', 25: '25%', 50: '50%', 75: '75%', 100: '100%' }} />
          </Form.Item>
          <Form.Item name="notes" label="备注说明">
            <TextArea rows={4} placeholder="记录本次进度的详细内容..." />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑进度记录模态框 */}
      <Modal
        title="编辑进度记录"
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          setCurrentRecord(null);
          form.resetFields();
        }}
        footer={null}
        width={500}
      >
        <Form form={form} layout="vertical" onFinish={handleEdit}>
          <Form.Item 
            name="progress" 
            label="进度百分比" 
            rules={[{ required: true, message: '请输入进度' }]}
          >
            <Slider marks={{ 0: '0%', 25: '25%', 50: '50%', 75: '75%', 100: '100%' }} />
          </Form.Item>
          <Form.Item name="notes" label="备注说明">
            <TextArea rows={4} placeholder="记录本次进度的详细内容..." />
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

export default ProgressHistory;
