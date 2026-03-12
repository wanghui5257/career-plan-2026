import React from 'react'
import { Card, Row, Col, Statistic } from 'antd'
import {
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'
import { Task, ProgressStats } from '../types/task'

interface ChartsProps {
  tasks: Task[]
  stats: ProgressStats
}

const Charts: React.FC<ChartsProps> = ({ tasks, stats }) => {
  // 任务状态分布数据（饼图）
  const statusData = [
    { name: '待办', value: tasks.filter((t) => t.status === 'todo').length, color: '#1890ff' },
    { name: '进行中', value: tasks.filter((t) => t.status === 'in_progress').length, color: '#faad14' },
    { name: '已完成', value: tasks.filter((t) => t.status === 'completed').length, color: '#52c41a' },
    { name: '已阻塞', value: tasks.filter((t) => t.status === 'blocked').length, color: '#ff4d4f' },
  ].filter((item) => item.value > 0)

  // 每日任务完成数（柱状图）- 模拟数据
  const dailyData = [
    { date: '3-06', completed: 2 },
    { date: '3-07', completed: 1 },
    { date: '3-08', completed: 3 },
    { date: '3-09', completed: 2 },
    { date: '3-10', completed: 4 },
    { date: '3-11', completed: 3 },
    { date: '3-12', completed: stats.completedTasks },
  ]

  // 完成率趋势（折线图）- 模拟数据
  const trendData = [
    { date: '3-06', rate: 20 },
    { date: '3-07', rate: 25 },
    { date: '3-08', rate: 35 },
    { date: '3-09', rate: 45 },
    { date: '3-10', rate: 55 },
    { date: '3-11', rate: 65 },
    { date: '3-12', rate: stats.completionRate },
  ]

  // 优先级分布
  const priorityData = [
    { name: '紧急', value: tasks.filter((t) => t.priority === 'urgent').length, color: '#ff4d4f' },
    { name: '高', value: tasks.filter((t) => t.priority === 'high').length, color: '#faad14' },
    { name: '中', value: tasks.filter((t) => t.priority === 'medium').length, color: '#1890ff' },
    { name: '低', value: tasks.filter((t) => t.priority === 'low').length, color: '#d9d9d9' },
  ].filter((item) => item.value > 0)

  // 计算逾期任务数
  const overdueTasks = tasks.filter((task) => {
    if (!task.dueDate || task.status === 'completed') return false
    return new Date(task.dueDate) < new Date()
  }).length

  return (
    <div style={{ padding: '24px' }}>
      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="总任务数"
              value={stats.totalTasks}
              suffix="个"
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="完成率"
              value={stats.completionRate}
              suffix="%"
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="进行中"
              value={stats.inProgressTasks}
              suffix="个"
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="逾期任务"
              value={overdueTasks}
              suffix="个"
              valueStyle={{ color: overdueTasks > 0 ? '#ff4d4f' : '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 图表区域 */}
      <Row gutter={[16, 16]}>
        {/* 状态分布饼图 */}
        <Col xs={24} md={12} lg={8}>
          <Card title="任务状态分布" style={{ height: '100%' }}>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  labelLine
                  label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        {/* 优先级分布饼图 */}
        <Col xs={24} md={12} lg={8}>
          <Card title="任务优先级分布" style={{ height: '100%' }}>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={priorityData}
                  cx="50%"
                  cy="50%"
                  labelLine
                  label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {priorityData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        {/* 每日完成任务数柱状图 */}
        <Col xs={24} md={12} lg={8}>
          <Card title="每日完成任务数" style={{ height: '100%' }}>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={dailyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="completed" fill="#52c41a" name="完成数" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        {/* 完成率趋势折线图 */}
        <Col xs={24} lg={12}>
          <Card title="完成率趋势" style={{ height: '100%' }}>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={trendData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis domain={[0, 100]} />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="rate"
                  stroke="#1890ff"
                  strokeWidth={2}
                  name="完成率 (%)"
                  dot={{ r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        {/* 任务进度热力图（简化版） */}
        <Col xs={24} lg={12}>
          <Card title="本周任务密度">
            <div style={{ padding: '20px 0' }}>
              <Row gutter={[8, 8]}>
                {['周一', '周二', '周三', '周四', '周五', '周六', '周日'].map((day, index) => {
                  const dayTasks = tasks.filter((task) => {
                    if (!task.dueDate) return false
                    const dueDate = new Date(task.dueDate)
                    return dueDate.getDay() === index + 1
                  }).length
                  const intensity = Math.min(dayTasks / 5, 1)
                  const color = `rgba(82, 196, 26, ${0.2 + intensity * 0.8})`

                  return (
                    <Col key={day} xs={3}>
                      <div
                        style={{
                          background: color,
                          borderRadius: '4px',
                          padding: '20px 10px',
                          textAlign: 'center',
                          minHeight: '80px',
                        }}
                      >
                        <div style={{ fontSize: '12px', color: '#666', marginBottom: '8px' }}>
                          {day}
                        </div>
                        <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#52c41a' }}>
                          {dayTasks}
                        </div>
                      </div>
                    </Col>
                  )
                })}
              </Row>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Charts
