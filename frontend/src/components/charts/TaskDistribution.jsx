import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from 'antd';

/**
 * 任务分布图表组件
 * 显示任务状态分布（柱状图）
 */
const TaskDistribution = ({ tasks = [] }) => {
  // 统计数据
  const statusCount = {
    todo: tasks.filter(t => t.status === 'todo').length,
    doing: tasks.filter(t => t.status === 'doing').length,
    completed: tasks.filter(t => t.status === 'completed').length,
  };

  const option = {
    title: {
      text: '任务状态分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
      },
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: ['待办', '进行中', '已完成'],
      axisTick: {
        alignWithLabel: true,
      },
      axisLabel: {
        fontSize: 14,
      },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
    },
    series: [
      {
        name: '任务数',
        type: 'bar',
        barWidth: '50%',
        data: [
          {
            value: statusCount.todo,
            name: '待办',
            itemStyle: { color: '#faad14' },
          },
          {
            value: statusCount.doing,
            name: '进行中',
            itemStyle: { color: '#1890ff' },
          },
          {
            value: statusCount.completed,
            name: '已完成',
            itemStyle: { color: '#52c41a' },
          },
        ],
        label: {
          show: true,
          position: 'top',
          fontSize: 14,
        },
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

export default TaskDistribution;
