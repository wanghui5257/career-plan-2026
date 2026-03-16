import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { Task, ProgressStats } from '../types/task'

// 筛选条件类型
export interface FilterState {
  status: Task['status'] | 'all'
  priority: Task['priority'] | 'all'
  assignee: string | 'all'
  search: string
  tags: string[]
}

// 主题设置类型
export interface ThemeState {
  mode: 'light' | 'dark'
  primaryColor: string
}

// 应用状态接口
interface AppState {
  // 任务相关
  tasks: Task[]
  filteredTasks: Task[]
  
  // 筛选条件
  filters: FilterState
  
  // 主题设置
  theme: ThemeState
  
  // 用户信息
  currentUser: {
    id: string
    name: string
    avatar?: string
  } | null
  
  // Actions - 任务操作
  setTasks: (tasks: Task[]) => void
  addTask: (task: Task) => void
  updateTask: (task: Task) => void
  deleteTask: (taskId: string) => void
  
  // Actions - 筛选操作
  setFilters: (filters: Partial<FilterState>) => void
  applyFilters: () => void
  
  // Actions - 主题操作
  setTheme: (theme: Partial<ThemeState>) => void
  
  // Actions - 用户操作
  setCurrentUser: (user: { id: string; name: string; avatar?: string }) => void
  
  // 工具函数
  calculateStats: () => ProgressStats
}

// 初始筛选状态
const initialFilters: FilterState = {
  status: 'all',
  priority: 'all',
  assignee: 'all',
  search: '',
  tags: [],
}

// 初始主题状态
const initialTheme: ThemeState = {
  mode: 'light',
  primaryColor: '#1890ff',
}

// 创建 store
export const useTaskStore = create<AppState>()(
  persist(
    (set, get) => ({
      // 初始状态
      tasks: [],
      filteredTasks: [],
      filters: initialFilters,
      theme: initialTheme,
      currentUser: null,

      // 设置任务列表
      setTasks: (tasks) => {
        set({ tasks })
        get().applyFilters()
      },

      // 添加任务
      addTask: (task) => {
        const tasks = get().tasks
        set({ tasks: [...tasks, task] })
        get().applyFilters()
      },

      // 更新任务
      updateTask: (updatedTask) => {
        const tasks = get().tasks.map((task) =>
          task.id === updatedTask.id ? updatedTask : task
        )
        set({ tasks })
        get().applyFilters()
      },

      // 删除任务
      deleteTask: (taskId) => {
        const tasks = get().tasks.filter((task) => task.id !== taskId)
        set({ tasks })
        get().applyFilters()
      },

      // 设置筛选条件
      setFilters: (newFilters) => {
        set((state) => ({
          filters: { ...state.filters, ...newFilters },
        }))
        get().applyFilters()
      },

      // 应用筛选
      applyFilters: () => {
        const { tasks, filters } = get()
        
        let filtered = [...tasks]

        // 按状态筛选
        if (filters.status !== 'all') {
          filtered = filtered.filter((task) => task.status === filters.status)
        }

        // 按优先级筛选
        if (filters.priority !== 'all') {
          filtered = filtered.filter((task) => task.priority === filters.priority)
        }

        // 按负责人筛选
        if (filters.assignee !== 'all') {
          filtered = filtered.filter((task) => task.assignee === filters.assignee)
        }

        // 按搜索词筛选
        if (filters.search) {
          const searchLower = filters.search.toLowerCase()
          filtered = filtered.filter(
            (task) =>
              task.title.toLowerCase().includes(searchLower) ||
              task.description.toLowerCase().includes(searchLower)
          )
        }

        // 按标签筛选
        if (filters.tags.length > 0) {
          filtered = filtered.filter((task) =>
            filters.tags.some((tag) => task.tags.includes(tag))
          )
        }

        set({ filteredTasks: filtered })
      },

      // 设置主题
      setTheme: (newTheme) => {
        set((state) => ({
          theme: { ...state.theme, ...newTheme },
        }))
      },

      // 设置当前用户
      setCurrentUser: (user) => {
        set({ currentUser: user })
      },

      // 计算统计信息
      calculateStats: (): ProgressStats => {
        const tasks = get().tasks
        const totalTasks = tasks.length
        const completedTasks = tasks.filter((t) => t.status === 'completed').length
        const inProgressTasks = tasks.filter((t) => t.status === 'in_progress').length
        const blockedTasks = tasks.filter((t) => t.status === 'blocked').length
        const completionRate =
          totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0

        return {
          totalTasks,
          completedTasks,
          inProgressTasks,
          blockedTasks,
          completionRate,
        }
      },
    }),
    {
      name: 'career-plan-storage', // localStorage 键名
      partialize: (state) => ({
        // 只持久化部分状态
        theme: state.theme,
        filters: state.filters,
        currentUser: state.currentUser,
      }),
    }
  )
)

export default useTaskStore
