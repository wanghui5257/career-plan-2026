import React from 'react';
import './ResponsiveTable.css';

/**
 * 响应式表格组件
 * - 桌面端：标准表格布局
 * - 移动端：转换为卡片布局
 */
const ResponsiveTable = ({ 
  columns = [], 
  dataSource = [], 
  rowKey = 'id',
  onRowClick
}) => {
  // 移动端卡片视图
  const MobileCardView = ({ record }) => (
    <div className="mobile-table-card">
      {columns.map((column, index) => {
        const value = typeof column.dataIndex === 'function' 
          ? column.dataIndex(record) 
          : record[column.dataIndex];
        
        return (
          <div key={index} className="mobile-table-row">
            {column.title && (
              <span className="mobile-table-label">{column.title}</span>
            )}
            <span className="mobile-table-value">
              {column.render ? column.render(value, record, index) : value}
            </span>
          </div>
        );
      })}
    </div>
  );

  return (
    <div className="responsive-table-container">
      {/* 桌面端表格视图 */}
      <div className="desktop-table-wrapper">
        <table className="responsive-table">
          <thead>
            <tr>
              {columns.map((column, index) => (
                <th 
                  key={index}
                  style={{ width: column.width }}
                >
                  {column.title}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {dataSource.map((record, rowIndex) => (
              <tr 
                key={typeof rowKey === 'function' ? rowKey(record) : record[rowKey]}
                onClick={() => onRowClick && onRowClick(record)}
                className={onRowClick ? 'clickable-row' : ''}
              >
                {columns.map((column, colIndex) => {
                  const value = typeof column.dataIndex === 'function' 
                    ? column.dataIndex(record) 
                    : record[column.dataIndex];
                  
                  return (
                    <td 
                      key={colIndex}
                      data-label={column.title}
                    >
                      {column.render ? column.render(value, record, rowIndex) : value}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 移动端卡片视图 */}
      <div className="mobile-table-wrapper">
        {dataSource.map((record, index) => (
          <MobileCardView 
            key={typeof rowKey === 'function' ? rowKey(record) : record[rowKey]} 
            record={record} 
          />
        ))}
      </div>
    </div>
  );
};

export default ResponsiveTable;
