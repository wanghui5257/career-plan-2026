import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, message, Spin } from 'antd';
import { UserOutlined, MailOutlined, EditOutlined } from '@ant-design/icons';
import AuthService from '../services/AuthService';
import UserService from '../services/UserService';

const Profile = () => {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [user, setUser] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setLoading(true);
    try {
      const data = await UserService.getProfile();
      setUser(data);
      form.setFieldsValue({
        username: data.username,
        email: data.email,
        background: data.background
      });
    } catch (error) {
      message.error('加载用户信息失败');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values) => {
    setSaving(true);
    try {
      await UserService.updateProfile(values);
      message.success('保存成功');
      loadProfile();
    } catch (error) {
      message.error('保存失败: ' + (error.message || '未知错误'));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '24px', maxWidth: '600px', margin: '0 auto' }}>
      <Card title="👤 个人中心" extra={<EditOutlined />}>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
        >
          <Form.Item name="username" label="用户名">
            <Input prefix={<UserOutlined />} disabled />
          </Form.Item>
          
          <Form.Item 
            name="email" 
            label="邮箱"
            rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
          >
            <Input prefix={<MailOutlined />} placeholder="请输入邮箱" />
          </Form.Item>
          
          <Form.Item name="background" label="背景介绍">
            <Input.TextArea rows={4} placeholder="介绍一下你自己..." />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={saving} block>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Profile;
