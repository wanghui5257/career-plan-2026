import React from 'react';
import ReactECharts from 'echarts-for-react';
import { Card } from 'antd';

/**
 * 统计趋势图表组件
 * 显示进度趋势（折线图）
 */
const StatsChart = ({ history = [] }) => {
  // 模拟数据 - 实际应从 API 获取
  const defaultHistory = [
    { date: '2026-03-11', completed: 5, total: 20 },
    { date: '2026-03-12', completed: 8, total: 20 },
    { date: '2026-03-13', completed: 10, total: 20 },
    { date: '2026-03-14', completed: 12, total: 20 },
    { date: '2026-03-15', completed: 13, total: 20 },
    { date: '2026-03-16', completed: 15, total: 20 },
    { date: '2026-03-17', completed: 18, total: 20 },
  ];

  const data = history.length > 0 ? history : defaultHistory;
  const dates = data.map(item => item.date.slice(5)); // 只显示 MM-DD
  const completedData = data.map(item => item.completed);
  const totalData = data.map(item => item.total);

  const option = {
    title: {
      text: '进度趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
      },
    },
    tooltip: {
      trigger: 'axis',
    },
    legend: {
      data: ['已完成', '总任务数'],
      top: '10%',
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '20%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        fontSize: 12,
      },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
    },
    series: [
      {
        name: '已完成',
        type: 'line',
        data: completedData,
        smooth: true,
        itemStyle: { color: '#52c41a' },
        areaStyle: {
          color: 'rgba(82, 196, 26, 0.2)',
        },
      },
      {
        name: '总任务数',
        type: 'line',
        data: totalData,
        smooth: true,
        itemStyle: { color: '#1890ff' },
        lineStyle: {
          type: 'dashed',
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

export default StatsChart;
