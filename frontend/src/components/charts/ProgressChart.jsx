import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card, Row, Col } from 'antd';

/**
 * 进度图表组件
 * 显示计划进度（饼图）
 */
const ProgressChart = ({ plans = [] }) => {
  // 统计数据
  const stats = {
    completed: plans.filter(p => p.progress === 100).length,
    inProgress: plans.filter(p => p.progress > 0 && p.progress < 100).length,
    notStarted: plans.filter(p => p.progress === 0).length,
  };

  const option = {
    title: {
      text: '计划进度分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
      },
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      data: ['已完成', '进行中', '未开始'],
    },
    series: [
      {
        name: '计划进度',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold',
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: stats.completed, name: '已完成', itemStyle: { color: '#52c41a' } },
          { value: stats.inProgress, name: '进行中', itemStyle: { color: '#1890ff' } },
          { value: stats.notStarted, name: '未开始', itemStyle: { color: '#d9d9d9' } },
        ],
      },
    ],
  };

  return (
    <Card className="chart-card">
      <ReactECharts
        option={option}
        style={{ height: 300 }}
        opts={{ renderer: 'canvas' }}
      />
    </Card>
  );
};

export default ProgressChart;
