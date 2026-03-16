import React, { useState } from 'react'
import { Task, BoardColumn, TaskStatus } from '../types/task'
import TaskCard from './TaskCard'

interface TaskBoardProps {
  tasks: Task[]
  onTaskUpdate?: (task: Task) => void
  onTaskCreate?: () => void
  onTaskEdit?: (task: Task) => void
}

const columns: BoardColumn[] = [
  { id: 'todo', title: '待办', color: '#6b7280' },
  { id: 'in_progress', title: '进行中', color: '#3b82f6' },
  { id: 'completed', title: '已完成', color: '#22c55e' },
  { id: 'blocked', title: '阻塞', color: '#ef4444' },
]

const TaskBoard: React.FC<TaskBoardProps> = ({ tasks, onTaskUpdate, onTaskEdit }) => {
  const [draggedTask, setDraggedTask] = useState<Task | null>(null)

  const handleDragStart = (task: Task) => {
    setDraggedTask(task)
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
  }

  const handleDrop = (status: TaskStatus) => {
    if (draggedTask && draggedTask.status !== status && onTaskUpdate) {
      const updatedTask = { ...draggedTask, status }
      onTaskUpdate(updatedTask)
    }
    setDraggedTask(null)
  }

  return (
    <div className="task-board">
      {columns.map((column) => {
        const columnTasks = tasks.filter((task) => task.status === column.id)
        return (
          <div
            key={column.id}
            className="board-column"
            onDragOver={handleDragOver}
            onDrop={() => handleDrop(column.id)}
            style={{ borderTop: `4px solid ${column.color}` }}
          >
            <div className="column-header" style={{ borderBottomColor: column.color }}>
              <span className="column-title">{column.title}</span>
              <span className="task-count">{columnTasks.length}</span>
            </div>
            <div>
              {columnTasks.map((task) => (
                <div
                  key={task.id}
                  draggable
                  onDragStart={() => handleDragStart(task)}
                >
                  <TaskCard task={task} onUpdate={onTaskUpdate} onEdit={onTaskEdit} />
                </div>
              ))}
              {columnTasks.length === 0 && (
                <div style={{ textAlign: 'center', padding: '24px', color: '#9ca3af' }}>
                  暂无任务
                </div>
              )}
            </div>
          </div>
        )
      })}
    </div>
  )
}

export default TaskBoard
