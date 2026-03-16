import React, { useState, useEffect } from 'react'
import {
  Modal,
  Form,
  Input,
  Select,
  DatePicker,
  Slider,
  Tag,
  Button,
  Space,
  message,
  Upload,
} from 'antd'
import {
  UploadOutlined,
  DeleteOutlined,
} from '@ant-design/icons'
import type { UploadFile } from 'antd'
import { Task } from '../types/task'

interface TaskEditorProps {
  isOpen: boolean
  task: Task | null
  isCreateMode?: boolean
  onClose: () => void
  onSave: (task: Task) => void
  onDelete?: (taskId: string) => void
}

const TaskEditor: React.FC<TaskEditorProps> = ({
  isOpen,
  task,
  isCreateMode = false,
  onClose,
  onSave,
  onDelete,
}) => {
  const [form] = Form.useForm()
  const [fileList, setFileList] = useState<UploadFile[]>([])

  // 重置表单
  useEffect(() => {
    if (isOpen && task) {
      form.setFieldsValue({
        title: task.title,
        description: task.description,
        status: task.status,
        priority: task.priority,
        progress: task.progress,
        startDate: task.startDate,
        dueDate: task.dueDate,
        assignee: task.assignee,
        tags: task.tags,
      })
    } else if (isOpen && isCreateMode) {
      form.resetFields()
      form.setFieldsValue({
        status: 'todo',
        priority: 'medium',
        progress: 0,
        assignee: 'alice',
        tags: [],
      })
    }
  }, [isOpen, task, isCreateMode, form])

  // 处理保存
  const handleSave = () => {
    form
      .validateFields()
      .then((values) => {
        const savedTask: Task = {
          id: task?.id || `task-${Date.now()}`,
          title: values.title,
          description: values.description || '',
          status: values.status,
          priority: values.priority,
          assignee: values.assignee,
          progress: values.progress,
          startDate: values.startDate?.format('YYYY-MM-DD'),
          dueDate: values.dueDate?.format('YYYY-MM-DD'),
          tags: values.tags || [],
          createdAt: task?.createdAt || new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }
        onSave(savedTask)
        message.success(isCreateMode ? '任务创建成功' : '任务保存成功')
      })
      .catch((info) => {
        console.log('验证失败:', info)
      })
  }

  // 处理删除
  const handleDelete = () => {
    if (task?.id && onDelete) {
      Modal.confirm({
        title: '确认删除',
        content: '确定要删除这个任务吗？此操作不可恢复。',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          onDelete(task.id)
          message.success('任务已删除')
        },
      })
    }
  }

  // 自定义标签选择
  const tagOptions = ['前端', '后端', 'UI', 'API', '调研', '学习', '职业规划', '测试', '文档']

  return (
    <Modal
      title={isCreateMode ? '新建任务' : '编辑任务'}
      open={isOpen}
      onCancel={onClose}
      width={700}
      footer={[
        <Button key="cancel" onClick={onClose}>
          取消
        </Button>,
        !isCreateMode && onDelete && (
          <Button key="delete" danger icon={<DeleteOutlined />} onClick={handleDelete}>
            删除
          </Button>
        ),
        <Button key="save" type="primary" onClick={handleSave}>
          保存
        </Button>,
      ]}
    >
      <Form form={form} layout="vertical" size="large">
        {/* 标题 */}
        <Form.Item
          label="标题"
          name="title"
          rules={[{ required: true, message: '请输入任务标题' }]}
        >
          <Input placeholder="输入任务标题" />
        </Form.Item>

        {/* 描述 */}
        <Form.Item label="描述" name="description">
          <Input.TextArea
            rows={4}
            placeholder="详细描述任务内容、目标和要求"
            showCount
            maxLength={1000}
          />
        </Form.Item>

        {/* 状态和优先级 */}
        <Form.Item label="状态" name="status" rules={[{ required: true }]}>
          <Select>
            <Select.Option value="todo">
              <Tag color="blue">待办</Tag>
            </Select.Option>
            <Select.Option value="in_progress">
              <Tag color="orange">进行中</Tag>
            </Select.Option>
            <Select.Option value="completed">
              <Tag color="green">已完成</Tag>
            </Select.Option>
            <Select.Option value="blocked">
              <Tag color="red">已阻塞</Tag>
            </Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="优先级" name="priority" rules={[{ required: true }]}>
          <Select>
            <Select.Option value="low">
              <Tag>低</Tag>
            </Select.Option>
            <Select.Option value="medium">
              <Tag color="blue">中</Tag>
            </Select.Option>
            <Select.Option value="high">
              <Tag color="orange">高</Tag>
            </Select.Option>
            <Select.Option value="urgent">
              <Tag color="red">紧急</Tag>
            </Select.Option>
          </Select>
        </Form.Item>

        {/* 进度条 */}
        <Form.Item label="进度" name="progress">
          <Slider
            min={0}
            max={100}
            marks={{
              0: '0%',
              25: '25%',
              50: '50%',
              75: '75%',
              100: '100%',
            }}
            tooltip={{ formatter: (value) => `${value}%` }}
          />
        </Form.Item>

        {/* 日期选择 */}
        <Space.Compact block>
          <Form.Item label="开始日期" name="startDate" style={{ flex: 1, marginBottom: 0 }}>
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item label="截止日期" name="dueDate" style={{ flex: 1, marginBottom: 0 }}>
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
        </Space.Compact>

        {/* 负责人 */}
        <Form.Item label="负责人" name="assignee">
          <Select>
            <Select.Option value="alice">Alice (前端)</Select.Option>
            <Select.Option value="backend-dev">Backend Dev (后端)</Select.Option>
            <Select.Option value="ai-collection">AI Collection</Select.Option>
            <Select.Option value="career-advisor">Career Advisor</Select.Option>
            <Select.Option value="learning-coach">Learning Coach</Select.Option>
          </Select>
        </Form.Item>

        {/* 标签 */}
        <Form.Item label="标签" name="tags">
          <Select
            mode="tags"
            placeholder="选择或输入标签"
            tokenSeparators={[',']}
            options={tagOptions.map((tag) => ({ label: tag, value: tag }))}
          />
        </Form.Item>

        {/* 附件上传（可选功能） */}
        <Form.Item label="附件">
          <Upload
            fileList={fileList}
            onChange={({ fileList }) => setFileList(fileList)}
            multiple
            maxCount={5}
          >
            <Button icon={<UploadOutlined />}>选择文件</Button>
          </Upload>
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default TaskEditor
