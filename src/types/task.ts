// 任务状态枚举
export type TaskStatus = 'todo' | 'in_progress' | 'completed' | 'blocked'

// 任务优先级
export type TaskPriority = 'low' | 'medium' | 'high' | 'urgent'

// 任务接口定义
export interface Task {
  id: string
  title: string
  description: string
  status: TaskStatus
  priority: TaskPriority
  assignee: string
  startDate?: string
  dueDate?: string
  progress: number // 0-100
  tags: string[]
  createdAt: string
  updatedAt: string
}

// 任务看板列定义
export interface BoardColumn {
  id: TaskStatus
  title: string
  color: string
}

// 进度统计
export interface ProgressStats {
  totalTasks: number
  completedTasks: number
  inProgressTasks: number
  blockedTasks: number
  completionRate: number
}

// 用户接口
export interface User {
  id: string
  name: string
  avatar?: string
  role: string
}
