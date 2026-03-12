import React, { useState, useRef } from 'react'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { Button, Space, Tag } from 'antd'
import { LeftOutlined, RightOutlined, CalendarOutlined } from '@ant-design/icons'
import { Task } from '../types/task'
import TaskEditor from './TaskEditor'

interface CalendarViewProps {
  tasks: Task[]
  onTaskUpdate: (task: Task) => void
  onTaskCreate: (task: Omit<Task, 'id' | 'createdAt' | 'updatedAt'>) => void
}

const CalendarView: React.FC<CalendarViewProps> = ({ tasks, onTaskUpdate, onTaskCreate }) => {
  const [currentView, setCurrentView] = useState('dayGridMonth')
  const [selectedTask, setSelectedTask] = useState<Task | null>(null)
  const [isEditorOpen, setIsEditorOpen] = useState(false)
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
  const calendarRef = useRef<FullCalendar>(null)

  // 将任务转换为日历事件
  const events = tasks.map((task) => ({
    id: task.id,
    title: task.title,
    start: task.startDate || task.createdAt,
    end: task.dueDate,
    backgroundColor: getTaskColor(task.status),
    borderColor: getTaskColor(task.status),
    extendedProps: { task },
  }))

  // 根据任务状态获取颜色
  const getTaskColor = (status: Task['status']) => {
    const colors = {
      todo: '#1890ff',
      in_progress: '#faad14',
      completed: '#52c41a',
      blocked: '#ff4d4f',
    }
    return colors[status]
  }

  // 处理事件点击
  const handleEventClick = (clickInfo: any) => {
    const task = clickInfo.event.extendedProps.task
    setSelectedTask(task)
    setIsEditorOpen(true)
  }

  // 处理日期拖拽
  const handleEventDrop = (dropInfo: any) => {
    const task = dropInfo.event.extendedProps.task
    const updatedTask: Task = {
      ...task,
      dueDate: dropInfo.event.end?.toISOString().split('T')[0] || task.dueDate,
      updatedAt: new Date().toISOString(),
    }
    onTaskUpdate(updatedTask)
  }

  // 处理日期选择（创建新任务）
  const handleDateSelect = (selectInfo: any) => {
    setSelectedTask({
      id: '',
      title: '',
      description: '',
      status: 'todo',
      priority: 'medium',
      assignee: 'alice',
      progress: 0,
      tags: [],
      startDate: selectInfo.startStr.split('T')[0],
      dueDate: selectInfo.endStr?.split('T')[0] || selectInfo.startStr.split('T')[0],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    })
    setIsCreateModalOpen(true)
  }

  // 切换到今天
  const handleToday = () => {
    const calendarApi = calendarRef.current?.getApi()
    if (calendarApi) {
      calendarApi.today()
    }
  }

  // 切换到上一周期
  const handlePrev = () => {
    const calendarApi = calendarRef.current?.getApi()
    if (calendarApi) {
      calendarApi.prev()
    }
  }

  // 切换到下一周期
  const handleNext = () => {
    const calendarApi = calendarRef.current?.getApi()
    if (calendarApi) {
      calendarApi.next()
    }
  }

  // 视图切换
  const handleViewChange = (view: string) => {
    setCurrentView(view)
  }

  // 自定义事件渲染
  const renderEventContent = (eventInfo: any) => {
    const task = eventInfo.event.extendedProps.task
    return (
      <div style={{ padding: '2px 4px', fontSize: '12px', overflow: 'hidden' }}>
        <strong>{eventInfo.event.title}</strong>
        {task && (
          <div style={{ display: 'flex', gap: '2px', marginTop: '2px' }}>
            <Tag color={getTaskColor(task.status)} style={{ fontSize: '10px' }}>
              {task.status}
            </Tag>
            {task.priority === 'high' && (
              <Tag color="red" style={{ fontSize: '10px' }}>
                高
              </Tag>
            )}
          </div>
        )}
      </div>
    )
  }

  return (
    <div style={{ padding: '24px', background: '#fff', borderRadius: '8px' }}>
      {/* 工具栏 */}
      <Space style={{ marginBottom: '16px', flexWrap: 'wrap' }} size="middle">
        <Button icon={<LeftOutlined />} onClick={handlePrev} />
        <Button onClick={handleToday}>今天</Button>
        <Button icon={<RightOutlined />} onClick={handleNext} />
        
        <Space style={{ marginLeft: '16px' }}>
          <Button
            type={currentView === 'dayGridMonth' ? 'primary' : 'default'}
            onClick={() => handleViewChange('dayGridMonth')}
          >
            月视图
          </Button>
          <Button
            type={currentView === 'dayGridWeek' ? 'primary' : 'default'}
            onClick={() => handleViewChange('dayGridWeek')}
          >
            周视图
          </Button>
          <Button
            type={currentView === 'dayGridDay' ? 'primary' : 'default'}
            onClick={() => handleViewChange('dayGridDay')}
          >
            日视图
          </Button>
        </Space>

        <Button
          type="primary"
          icon={<CalendarOutlined />}
          style={{ marginLeft: 'auto' }}
          onClick={() => {
            setSelectedTask(null)
            setIsCreateModalOpen(true)
          }}
        >
          新建任务
        </Button>
      </Space>

      {/* 日历组件 */}
      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView={currentView}
        headerToolbar={false}
        events={events}
        selectable={true}
        editable={true}
        eventClick={handleEventClick}
        eventDrop={handleEventDrop}
        select={handleDateSelect}
        eventContent={renderEventContent}
        height="auto"
        locale="zh-cn"
        firstDay={1}
        buttonText={{
          today: '今天',
          month: '月',
          week: '周',
          day: '日',
        }}
        allDayText="全天"
      />

      {/* 任务编辑器模态框 */}
      <TaskEditor
        isOpen={isEditorOpen}
        task={selectedTask}
        onClose={() => {
          setIsEditorOpen(false)
          setSelectedTask(null)
        }}
        onSave={(task) => {
          if (task.id) {
            onTaskUpdate(task)
          } else {
            onTaskCreate({
              ...task,
              id: `task-${Date.now()}`,
            } as Task)
          }
          setIsEditorOpen(false)
          setSelectedTask(null)
        }}
      />

      {/* 创建任务模态框 */}
      <TaskEditor
        isOpen={isCreateModalOpen}
        task={selectedTask}
        isCreateMode={true}
        onClose={() => {
          setIsCreateModalOpen(false)
          setSelectedTask(null)
        }}
        onSave={(task) => {
          onTaskCreate({
            ...task,
            id: `task-${Date.now()}`,
          } as Task)
          setIsCreateModalOpen(false)
          setSelectedTask(null)
        }}
      />
    </div>
  )
}

export default CalendarView
