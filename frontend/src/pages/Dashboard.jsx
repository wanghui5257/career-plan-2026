import React, { useState } from 'react';
import { Card, Row, Col, Button, Progress, Statistic } from 'antd';
import { PlusOutlined, DashboardOutlined, FileTextOutlined, CheckCircleOutlined } from '@ant-design/icons';
import TaskCard from '../components/TaskCard';
import ResponsiveTable from '../components/ResponsiveTable';
import './Dashboard.css';

/**
 * Dashboard 主页组件
 * 显示计划列表、进度概览、快速创建任务入口
 */
const Dashboard = () => {
  // 模拟数据 - 实际应从 API 获取
  const [plans] = useState([
    {
      key: '1',
      name: 'AI 职业转型学习计划',
      status: '进行中',
      progress: 65,
      totalTasks: 20,
      completedTasks: 13,
      startDate: '2026-03-01',
      endDate: '2026-06-30'
    },
    {
      key: '2',
      name: '前端技能提升计划',
      status: '进行中',
      progress: 40,
      totalTasks: 15,
      completedTasks: 6,
      startDate: '2026-03-10',
      endDate: '2026-05-31'
    },
    {
      key: '3',
      name: '后端开发学习计划',
      status: '未开始',
      progress: 0,
      totalTasks: 18,
      completedTasks: 0,
      startDate: '2026-04-01',
      endDate: '2026-07-31'
    }
  ]);

  const [tasks] = useState([
    {
      key: '1',
      title: '学习 React Router',
      description: '掌握 React 路由配置和页面导航',
      status: 'completed',
      priority: 'high',
      dueDate: '2026-03-15'
    },
    {
      key: '2',
      title: '实现响应式导航栏',
      description: '创建支持移动端的响应式导航组件',
      status: 'completed',
      priority: 'high',
      dueDate: '2026-03-16'
    },
    {
      key: '3',
      title: '创建 Dashboard 页面',
      description: '整合所有组件，创建主页',
      status: 'doing',
      priority: 'high',
      dueDate: '2026-03-17'
    },
    {
      key: '4',
      title: '学习 Spring Security',
      description: '掌握 Spring Boot 安全认证',
      status: 'todo',
      priority: 'medium',
      dueDate: '2026-03-20'
    }
  ]);

  const tableColumns = [
    {
      title: '计划名称',
      dataIndex: 'name',
      key: 'name',
      width: 300
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        const colorMap = {
          '进行中': 'blue',
          '未开始': 'gray',
          '已完成': 'green'
        };
        return <span style={{ color: colorMap[status] || 'default' }}>{status}</span>;
      }
    },
    {
      title: '进度',
      key: 'progress',
      width: 200,
      render: (_, record) => (
        <Progress 
          percent={record.progress} 
          status={record.progress === 100 ? 'success' : 'active'}
          format={() => `${record.completedTasks}/${record.totalTasks}`}
        />
      )
    },
    {
      title: '开始日期',
      dataIndex: 'startDate',
      key: 'startDate',
      width: 120
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      key: 'endDate',
      width: 120
    }
  ];

  return (
    <div className="dashboard">
      {/* 快速操作区 */}
      <div className="quick-actions">
        <Button type="primary" icon={<PlusOutlined />} size="large">
          创建计划
        </Button>
        <Button type="default" icon={<PlusOutlined />} size="large">
          创建任务
        </Button>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} className="stats-row">
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="总计划数"
              value={plans.length}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="进行中"
              value={plans.filter(p => p.status === '进行中').length}
              prefix={<DashboardOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="总任务数"
              value={tasks.length}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="完成率"
              value={Math.round((tasks.filter(t => t.status === 'completed').length / tasks.length) * 100)}
              suffix="%"
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 计划列表 */}
      <Card 
        title="📋 计划列表" 
        className="section-card"
        extra={<Button type="link">查看全部</Button>}
      >
        <ResponsiveTable columns={tableColumns} dataSource={plans} />
      </Card>

      {/* 任务卡片 */}
      <Card 
        title="✅ 最近任务" 
        className="section-card"
        extra={<Button type="link">查看全部</Button>}
      >
        <Row gutter={[16, 16]}>
          {tasks.map(task => (
            <Col xs={24} sm={12} lg={8} xl={6} key={task.key}>
              <TaskCard
                title={task.title}
                description={task.description}
                status={task.status}
                priority={task.priority}
                dueDate={task.dueDate}
              />
            </Col>
          ))}
        </Row>
      </Card>
    </div>
  );
};

export default Dashboard;
