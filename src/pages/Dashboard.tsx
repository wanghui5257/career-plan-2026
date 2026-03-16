import React, { useState, useEffect } from 'react'
import {
  Layout,
  Menu,
  Button,
  Avatar,
  Dropdown,
  Space,
  Drawer,
  Input,
  type MenuProps,
} from 'antd'
import {
  DashboardOutlined,
  CalendarOutlined,
  BarChartOutlined,
  TeamOutlined,
  SettingOutlined,
  PlusOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  SunOutlined,
  MoonOutlined,
  SearchOutlined,
} from '@ant-design/icons'
import TaskBoard from '../components/TaskBoard'
import ProgressBar from '../components/ProgressBar'
import CalendarView from '../components/CalendarView'
import Charts from '../components/Charts'
import TaskEditor from '../components/TaskEditor'
import { useTaskStore } from '../store'
import { Task } from '../types/task'
import { message } from 'antd'
import { clearAuth } from '../utils/auth'

const { Header, Content, Sider } = Layout

// 模拟数据 - 后续会从 store 获取
const mockTasks: Task[] = [
  {
    id: '1',
    title: '设计 Web 界面原型',
    description: '任务列表、可编辑任务、进度图表',
    status: 'in_progress',
    priority: 'high',
    assignee: 'alice',
    progress: 60,
    tags: ['前端', 'UI'],
    startDate: '2026-03-09',
    dueDate: '2026-03-15',
    createdAt: '2026-03-09T08:00:00Z',
    updatedAt: '2026-03-09T08:30:00Z',
  },
  {
    id: '2',
    title: '设计 RESTful API 接口文档',
    description: 'OpenAPI/Swagger 规范',
    status: 'in_progress',
    priority: 'high',
    assignee: 'backend-dev',
    progress: 40,
    tags: ['后端', 'API'],
    startDate: '2026-03-09',
    dueDate: '2026-03-14',
    createdAt: '2026-03-09T08:00:00Z',
    updatedAt: '2026-03-09T08:30:00Z',
  },
  {
    id: '3',
    title: '竞品调研',
    description: 'Notion、Trello、Jira 功能分析',
    status: 'todo',
    priority: 'medium',
    assignee: 'ai-collection',
    progress: 0,
    tags: ['调研'],
    dueDate: '2026-03-18',
    createdAt: '2026-03-09T08:00:00Z',
    updatedAt: '2026-03-09T08:00:00Z',
  },
  {
    id: '4',
    title: 'AI 岗位需求分析',
    description: 'Java + AI 方向岗位分析',
    status: 'todo',
    priority: 'medium',
    assignee: 'career-advisor',
    progress: 0,
    tags: ['职业规划'],
    dueDate: '2026-03-20',
    createdAt: '2026-03-09T08:00:00Z',
    updatedAt: '2026-03-09T08:00:00Z',
  },
  {
    id: '5',
    title: '《沟通的方法》拆解',
    description: '400 页内容，6 周完成',
    status: 'completed',
    priority: 'high',
    assignee: 'learning-coach',
    progress: 100,
    tags: ['学习'],
    startDate: '2026-03-01',
    dueDate: '2026-03-10',
    createdAt: '2026-03-09T08:00:00Z',
    updatedAt: '2026-03-09T08:30:00Z',
  },
]

type ViewMode = 'board' | 'calendar' | 'charts'

const Dashboard: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const [viewMode, setViewMode] = useState<ViewMode>('board')
  const [isEditorOpen, setIsEditorOpen] = useState(false)
  const [selectedTask, setSelectedTask] = useState<Task | null>(null)
  const [isMobile, setIsMobile] = useState(false)
  
  // 使用 Zustand store
  const { 
    tasks, 
    setTasks, 
    addTask, 
    updateTask, 
    deleteTask,
    calculateStats,
    theme,
    setTheme,
  } = useTaskStore()

  // 初始化任务数据
  useEffect(() => {
    if (tasks.length === 0) {
      setTasks(mockTasks)
    }
  }, [tasks.length, setTasks])

  // 响应式检测
  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768)
      if (window.innerWidth < 768) {
        setCollapsed(true)
      }
    }
    
    checkMobile()
    window.addEventListener('resize', checkMobile)
    return () => window.removeEventListener('resize', checkMobile)
  }, [])

  // 计算进度统计
  const stats = calculateStats()

  // 处理任务更新
  const handleTaskUpdate = (updatedTask: Task) => {
    updateTask(updatedTask)
  }

  // 处理任务创建
  const handleTaskCreate = (newTask: Task) => {
    addTask(newTask)
  }

  // 处理任务删除
  const handleTaskDelete = (taskId: string) => {
    deleteTask(taskId)
  }

  // 打开任务编辑器
  const handleOpenEditor = (task?: Task) => {
    setSelectedTask(task || null)
    setIsEditorOpen(true)
  }

  // 退出登录
  const handleLogout = () => {
    // 清除本地存储
    clearAuth()
    message.success('已退出登录')
    // 使用 hash 路由跳转到登录页
    window.location.hash = '/login'
  }

  // 主题切换
  const toggleTheme = () => {
    setTheme({
      mode: theme.mode === 'light' ? 'dark' : 'light',
    })
  }

  // 侧边栏菜单点击处理
  const handleMenuClick: MenuProps['onClick'] = (e) => {
    switch (e.key) {
      case 'board':
        setViewMode('board')
        break
      case 'calendar':
        setViewMode('calendar')
        break
      case 'charts':
        setViewMode('charts')
        break
      case 'team':
        message.info('🚧 团队成员功能开发中，敬请期待！')
        break
      case 'settings':
        message.info('🚧 设置功能开发中，敬请期待！')
        break
    }
  }

  // 侧边栏菜单
  const menuItems: MenuProps['items'] = [
    {
      key: 'board',
      icon: <DashboardOutlined />,
      label: '任务看板',
    },
    {
      key: 'calendar',
      icon: <CalendarOutlined />,
      label: '日历视图',
    },
    {
      key: 'charts',
      icon: <BarChartOutlined />,
      label: '数据报表',
    },
    {
      type: 'divider',
    },
    {
      key: 'team',
      icon: <TeamOutlined />,
      label: '团队成员',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ]

  // 渲染当前视图
  const renderView = () => {
    switch (viewMode) {
      case 'calendar':
        return (
          <CalendarView
            tasks={tasks}
            onTaskUpdate={handleTaskUpdate}
            onTaskCreate={(taskData) => handleTaskCreate({ ...taskData, id: `task-${Date.now()}` } as Task)}
          />
        )
      case 'charts':
        return <Charts tasks={tasks} stats={stats} />
      case 'board':
      default:
        return (
          <TaskBoard
            tasks={tasks}
            onTaskUpdate={handleTaskUpdate}
            onTaskCreate={() => handleOpenEditor()}
            onTaskEdit={handleOpenEditor}
          />
        )
    }
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* 侧边栏 - 桌面端 */}
      {!isMobile && (
        <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
          <div
            style={{
              height: 32,
              margin: 16,
              background: 'rgba(255, 255, 255, 0.2)',
              borderRadius: 4,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
              fontSize: collapsed ? '12px' : '14px',
              fontWeight: 'bold',
            }}
          >
            {collapsed ? 'CP' : '职业计划 2026'}
          </div>
          <Menu theme="dark" mode="inline" selectedKeys={[viewMode]} onClick={handleMenuClick} items={menuItems} />
        </Sider>
      )}

      <Layout>
        <Header
          style={{
            padding: isMobile ? '0 12px' : '0 24px',
            background: '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <Space>
            {isMobile && (
              <Button
                type="text"
                icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                onClick={() => setCollapsed(!collapsed)}
              />
            )}
            <h2 style={{ margin: 0, fontSize: isMobile ? '16px' : '20px' }}>
              职业发展计划 2026
            </h2>
          </Space>

          <Space size="middle">
            {/* 主题切换 */}
            <Button
              type="text"
              icon={theme.mode === 'light' ? <MoonOutlined /> : <SunOutlined />}
              onClick={toggleTheme}
              title={theme.mode === 'light' ? '切换到暗色主题' : '切换到亮色主题'}
            />

            {/* 搜索框 - 桌面端显示 */}
            {!isMobile && (
              <Input
                placeholder="搜索任务..."
                prefix={<SearchOutlined />}
                style={{ width: 200 }}
                allowClear
              />
            )}

            {/* 新建任务按钮 */}
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => handleOpenEditor()}
            >
              {!isMobile && '新建任务'}
            </Button>

            {/* 用户头像 */}
            <Dropdown
              menu={{
                items: [
                  { key: 'profile', label: '个人中心', onClick: () => message.info('🚧 个人中心功能开发中，敬请期待！') },
                  { key: 'settings', label: '设置', onClick: () => message.info('🚧 设置功能开发中，敬请期待！') },
                  { type: 'divider' },
                  { key: 'logout', label: '退出登录', onClick: handleLogout },
                ],
              }}
            >
              <Avatar style={{ backgroundColor: '#1890ff', cursor: 'pointer' }}>
                A
              </Avatar>
            </Dropdown>
          </Space>
        </Header>

        <Content style={{ margin: '16px' }}>
          {/* 进度条 */}
          <ProgressBar stats={stats} />

          {/* 主视图区域 */}
          <div style={{ marginTop: '16px' }}>
            {renderView()}
          </div>
        </Content>
      </Layout>

      {/* 移动端抽屉菜单 */}
      <Drawer
        placement="left"
        onClose={() => setCollapsed(false)}
        open={isMobile && !collapsed}
        width={250}
      >
        <Menu mode="vertical" selectedKeys={[viewMode]} onClick={handleMenuClick} items={menuItems} />
      </Drawer>

      {/* 任务编辑器 */}
      <TaskEditor
        isOpen={isEditorOpen}
        task={selectedTask}
        isCreateMode={!selectedTask}
        onClose={() => {
          setIsEditorOpen(false)
          setSelectedTask(null)
        }}
        onSave={(task) => {
          if (task.id && tasks.find((t) => t.id === task.id)) {
            handleTaskUpdate(task)
          } else {
            handleTaskCreate(task)
          }
          setIsEditorOpen(false)
          setSelectedTask(null)
        }}
        onDelete={handleTaskDelete}
      />
    </Layout>
  )
}

export default Dashboard
