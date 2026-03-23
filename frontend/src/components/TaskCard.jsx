import React from 'react';
import './TaskCard.css';

const TaskCard = ({ 
  task,
  onClick,
  onStatusChange
}) => {
  // 空值保护
  if (!task) {
    return null;
  }
  
  const { 
    id, 
    title, 
    description, 
    status, 
    priority, 
    progress = 0,
    dueDate,
    assignee 
  } = task;

  const getStatusColor = (status) => {
    switch (status) {
      case 'DONE':
      case 'COMPLETED':
        return '#52c41a';
      case 'IN_PROGRESS':
        return '#1890ff';
      case 'TODO':
      case 'PENDING':
        return '#faad14';
      default:
        return '#d9d9d9';
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'HIGH':
      case 'URGENT':
        return <span className="priority-badge high">🔥 高</span>;
      case 'MEDIUM':
        return <span className="priority-badge medium">⚡ 中</span>;
      default:
        return <span className="priority-badge low">📌 低</span>;
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', { 
      month: 'short', 
      day: 'numeric' 
    });
  };

  return (
    <div 
      className={`task-card ${status?.toLowerCase() || ''}`}
      onClick={() => onClick && onClick(task)}
    >
      <div className="task-card-header">
        <h3 className="task-card-title">{title}</h3>
        {getPriorityBadge(priority)}
      </div>

      <div className="task-card-content">
        {description && (
          <p className="task-card-description">{description}</p>
        )}

        {progress !== undefined && (
          <div className="task-progress">
            <div className="task-progress-bar">
              <div 
                className="task-progress-fill" 
                style={{ 
                  width: `${progress}%`,
                  background: `linear-gradient(90deg, ${getStatusColor(status)} 0%, ${getStatusColor(status)} ${progress}%)`
                }}
              />
            </div>
            <span className="task-progress-text">{progress}%</span>
          </div>
        )}

        <div className="task-card-footer">
          <div className="task-meta">
            {dueDate && (
              <span className="task-due-date">
                📅 {formatDate(dueDate)}
              </span>
            )}
            {assignee && (
              <span className="task-assignee">
                👤 {assignee}
              </span>
            )}
          </div>
          
          <span 
            className="task-status-badge"
            style={{ background: getStatusColor(status) }}
          >
            {status === 'DONE' || status === 'COMPLETED' ? '✅ 完成' : 
             status === 'IN_PROGRESS' ? '🔄 进行中' : 
             status === 'TODO' ? '📝 待办' : status}
          </span>
        </div>
      </div>

      <div className="task-card-actions">
        {status !== 'DONE' && status !== 'COMPLETED' && (
          <button 
            className="action-btn complete"
            onClick={(e) => {
              e.stopPropagation();
              onStatusChange && onStatusChange(id, 'DONE');
            }}
          >
            ✅ 完成
          </button>
        )}
        <button 
          className="action-btn view"
          onClick={(e) => {
            e.stopPropagation();
            onClick && onClick(task);
          }}
        >
          👁 查看
        </button>
      </div>
    </div>
  );
};

export default TaskCard;
