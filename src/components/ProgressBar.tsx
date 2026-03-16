import React from 'react'
import { ProgressStats } from '../types/task'
import { Card, Progress, Statistic, Row, Col } from 'antd'

interface ProgressBarProps {
  stats: ProgressStats
}

const ProgressBar: React.FC<ProgressBarProps> = ({ stats }) => {
  return (
    <Card style={{ marginBottom: 24 }}>
      <Row gutter={16}>
        <Col span={6}>
          <Statistic
            title="总任务数"
            value={stats.totalTasks}
            suffix="个"
            valueStyle={{ color: '#1890ff' }}
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="完成率"
            value={stats.completionRate}
            suffix="%"
            valueStyle={{ color: '#52c41a' }}
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="进行中"
            value={stats.inProgressTasks}
            valueStyle={{ color: '#1890ff' }}
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="阻塞"
            value={stats.blockedTasks}
            valueStyle={{ color: '#ff4d4f' }}
          />
        </Col>
      </Row>
      <div style={{ marginTop: 24 }}>
        <Progress
          percent={stats.completionRate}
          strokeColor={{
            '0%': '#108ee9',
            '100%': '#87d068',
          }}
          status={stats.completionRate === 100 ? 'success' : 'active'}
        />
      </div>
    </Card>
  )
}

export default ProgressBar
