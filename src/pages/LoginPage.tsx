import React, { useState } from 'react'
import { Form, Input, Button, Card, message, Typography } from 'antd'
import { UserOutlined, LockOutlined, LoginOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { saveAuth } from '../utils/auth'

const { Title } = Typography

interface LoginFormData {
  username: string
  password: string
}

const LoginPage: React.FC = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

  // 调用后端 API 登录
  const handleLogin = async (values: LoginFormData) => {
    setLoading(true)
    try {
      // 调用后端登录 API
      const response = await fetch('https://plan.shujuyunxiang.com/back-server/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values)
      })
      
      const data = await response.json()
      
      if (response.ok && data.code === 200) {
        // 保存 token 和过期时间
        saveAuth(data.token, values.username, data.expiresIn)
        
        message.success('登录成功！')
        navigate('/')
      } else {
        message.error(data.message || '用户名或密码错误！')
      }
    } catch (error) {
      console.error('Login error:', error)
      message.error('登录失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    }}>
      <Card
        style={{
          width: 400,
          borderRadius: 16,
          boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={2} style={{ marginBottom: 8 }}>
            🎯 职业发展计划 2026
          </Title>
          <p style={{ color: '#666' }}>请登录以继续</p>
        </div>

        <Form
          name="login"
          onFinish={handleLogin}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
              icon={<LoginOutlined />}
            >
              登录
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', color: '#999', fontSize: 12 }}>
          <p>测试账号：admin / admin123</p>
        </div>
      </Card>
    </div>
  )
}

export default LoginPage
