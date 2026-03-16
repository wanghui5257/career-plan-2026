import React, { useState } from 'react'
import { Task, TaskPriority } from '../types/task'
import { Tag, Modal, Form, Input, Select, Button } from 'antd'

interface TaskCardProps {
  task: Task
  onUpdate?: (task: Task) => void
  onEdit?: (task: Task) => void
}

const priorityColors: Record<TaskPriority, string> = {
  urgent: 'red',
  high: 'orange',
  medium: 'gold',
  low: 'green',
}

const priorityLabels: Record<TaskPriority, string> = {
  urgent: '紧急',
  high: '高',
  medium: '中',
  low: '低',
}

const TaskCard: React.FC<TaskCardProps> = ({ task, onUpdate, onEdit }) => {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [form] = Form.useForm()

  const handleEdit = () => {
    // 如果父组件提供了 onEdit 回调，使用父组件的编辑器
    if (onEdit) {
      onEdit(task)
    } else {
      form.setFieldsValue(task)
      setIsModalOpen(true)
    }
  }

  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      const updatedTask = { ...task, ...values, updatedAt: new Date().toISOString() }
      onUpdate?.(updatedTask)
      setIsModalOpen(false)
    } catch (error) {
      console.error('Validation failed:', error)
    }
  }

  const getProgressColor = (progress: number) => {
    if (progress === 100) return '#22c55e'
    if (progress >= 70) return '#3b82f6'
    if (progress >= 30) return '#eab308'
    return '#ef4444'
  }

  return (
    <>
      <div className="task-card">
        <div className="task-card-header">
          <h3 className="task-card-title">{task.title}</h3>
          <Tag color={priorityColors[task.priority]}>{priorityLabels[task.priority]}</Tag>
        </div>
        {task.description && (
          <p className="task-card-description">{task.description}</p>
        )}
        <div className="progress-bar">
          <div
            className="progress-fill"
            style={{
              width: `${task.progress}%`,
              background: getProgressColor(task.progress),
            }}
          />
        </div>
        <div className="task-card-footer">
          <div className="task-card-tags">
            {task.tags.slice(0, 3).map((tag, index) => (
              <span key={index} className="task-tag">
                #{tag}
              </span>
            ))}
          </div>
          <Button type="link" size="small" onClick={handleEdit}>
            编辑
          </Button>
        </div>
      </div>

      <Modal
        title="编辑任务"
        open={isModalOpen}
        onOk={handleSave}
        onCancel={() => setIsModalOpen(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="title"
            label="任务标题"
            rules={[{ required: true, message: '请输入任务标题' }]}
          >
            <Input placeholder="输入任务标题" />
          </Form.Item>
          <Form.Item name="description" label="任务描述">
            <Input.TextArea rows={3} placeholder="输入任务描述" />
          </Form.Item>
          <Form.Item name="priority" label="优先级">
            <Select>
              <Select.Option value="low">低</Select.Option>
              <Select.Option value="medium">中</Select.Option>
              <Select.Option value="high">高</Select.Option>
              <Select.Option value="urgent">紧急</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="progress" label="进度">
            <Input type="number" min={0} max={100} addonAfter="%" />
          </Form.Item>
          <Form.Item name="assignee" label="负责人">
            <Input placeholder="输入负责人" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default TaskCard
