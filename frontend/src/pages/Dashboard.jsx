import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Progress, Statistic, Spin, Empty, Tabs, message } from 'antd';
import { PlusOutlined, DashboardOutlined, FileTextOutlined, CheckCircleOutlined } from '@ant-design/icons';
import TaskCard from '../components/TaskCard';
import ResponsiveTable from '../components/ResponsiveTable';
import { ProgressChart, TaskDistribution, StatsChart } from '../components/charts';
import PlanService from '../services/PlanService';
import ProgressService from '../services/ProgressService';
import TaskService from '../services/TaskService';
import './Dashboard.css';

/**
 * Dashboard 主页组件
 * 显示计划列表、进度概览、任务卡片、可视化图表
 */
const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [plans, setPlans] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [summary, setSummary] = useState(null);
  const [history, setHistory] = useState([]);

  // 加载数据
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // 获取计划列表
      const plansData = await PlanService.getPlans();
      setPlans(plansData || []);

      // 获取进度摘要
      const summaryData = await ProgressService.getProgressSummary();
      setSummary(summaryData);

      // 获取进度历史
      const historyData = await ProgressService.getProgressHistory();
      setHistory(historyData || []);

      // 从计划中提取任务，如果计划为空则直接获取任务列表
      let allTasks = [];
      if (plansData && plansData.length > 0) {
        plansData.forEach(plan => {
          if (plan.tasks) {
            plan.tasks.forEach(task => {
              allTasks.push({
                ...task,
                key: `${plan.id}-${task.id}`,
                planName: plan.name
              });
            });
          }
        });
      } else {
        // 如果没有计划，直接获取任务列表
        const tasksData = await TaskService.getTasks();
        if (tasksData) {
          allTasks = tasksData.map(task => ({
            ...task,
            key: `${task.id}`,
            planName: '-'
          }));
        }
      }
      setTasks(allTasks.slice(0, 4)); // 只显示最近 4 个任务
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

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
          percent={record.progress || 0} 
          status={record.progress === 100 ? 'success' : 'active'}
          format={() => `${record.completedTasks || 0}/${record.totalTasks || 0}`}
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

  if (loading) {
    return (
      <div className="dashboard-loading">
        <Spin size="large" tip="加载中..." />
      </div>
    );
  }

  return (
    <div className="dashboard">
      {/* 快速操作区 */}
      <div className="quick-actions">
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={() => message.info('创建计划功能开发中')}>
          创建计划
        </Button>
        <Button type="default" icon={<PlusOutlined />} size="large" onClick={() => message.info('创建任务功能开发中')}>
          创建任务
        </Button>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} className="stats-row">
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="总计划数"
              value={summary?.totalPlans || plans.length}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="进行中"
              value={summary?.ongoingPlans || (plans.length > 0 ? plans.filter(p => p.status === '进行中').length : tasks.filter(t => t.status === 'IN_PROGRESS').length)}
              prefix={<DashboardOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="总任务数"
              value={summary?.totalTasks || tasks.length}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stats-card">
            <Statistic
              title="完成率"
              value={summary?.completionRate || (tasks.length > 0 ? Math.round((tasks.filter(t => t.status === 'DONE' || t.status === 'COMPLETED').length / tasks.length) * 100) : 0)}
              suffix="%"
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 可视化图表 */}
      <Tabs
        defaultActiveKey="overview"
        items={[
          {
            key: 'overview',
            label: '📊 总览',
            children: (
              <Row gutter={[16, 16]}>
                <Col xs={24} lg={12}>
                  <ProgressChart plans={plans} />
                </Col>
                <Col xs={24} lg={12}>
                  <TaskDistribution tasks={tasks} />
                </Col>
              </Row>
            ),
          },
          {
            key: 'trend',
            label: '📈 趋势',
            children: <StatsChart history={history} />,
          },
        ]}
      />

      {/* 计划列表 */}
      <Card 
        title="📋 计划列表" 
        className="section-card"
        extra={<Button type="link">查看全部</Button>}
      >
        {plans.length > 0 ? (
          <ResponsiveTable columns={tableColumns} dataSource={plans} />
        ) : (
          <Empty description="暂无计划" />
        )}
      </Card>

      {/* 任务卡片 */}
      <Card 
        title="✅ 最近任务" 
        className="section-card"
        extra={<Button type="link">查看全部</Button>}
      >
        {tasks.length > 0 ? (
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
        ) : (
          <Empty description="暂无任务" />
        )}
      </Card>
    </div>
  );
};

export default Dashboard;
