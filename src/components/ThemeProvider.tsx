import React, { useEffect } from 'react'
import { ConfigProvider, theme } from 'antd'
import { useTaskStore } from '../store'

interface ThemeProviderProps {
  children: React.ReactNode
}

const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
  const { theme: themeState, setTheme } = useTaskStore()

  // 从系统获取主题偏好
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    
    // 如果是首次加载且没有持久化的主题，使用系统偏好
    const storedTheme = localStorage.getItem('career-plan-storage')
    if (!storedTheme) {
      setTheme({
        mode: mediaQuery.matches ? 'dark' : 'light',
      })
    }

    // 监听系统主题变化
    const handleChange = (e: MediaQueryListEvent) => {
      setTheme({
        mode: e.matches ? 'dark' : 'light',
      })
    }

    mediaQuery.addEventListener('change', handleChange)
    return () => mediaQuery.removeEventListener('change', handleChange)
  }, [setTheme])

  // Ant Design 主题配置
  const antdTheme = {
    algorithm: themeState.mode === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      colorPrimary: themeState.primaryColor,
      borderRadius: 6,
    },
  }

  return (
    <ConfigProvider theme={antdTheme}>
      {children}
    </ConfigProvider>
  )
}

export default ThemeProvider
