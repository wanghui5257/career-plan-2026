import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, message, Spin, Tag, Descriptions } from 'antd';
import { UserOutlined, MailOutlined, PhoneOutlined, IdcardOutlined, EditOutlined, SaveOutlined } from '@ant-design/icons';
import AuthService from '../services/AuthService';
import UserService from '../services/UserService';

const Profile = () => {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [user, setUser] = useState(null);
  const [editing, setEditing] = useState(false);
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
      setEditing(false);
      loadProfile();
    } catch (error) {
      message.error('保存失败: ' + (error.message || '未知错误'));
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setEditing(false);
    form.setFieldsValue({
      email: user?.email,
      background: user?.background
    });
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <Spin size="large" />
      </div>
    );
  }

  const getRoleTag = (role) => {
    const roleConfig = {
      'ADMIN': { color: 'red', text: '管理员' },
      'USER': { color: 'blue', text: '普通用户' }
    };
    const config = roleConfig[role] || { color: 'default', text: role };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <Card 
        title="👤 个人中心" 
        extra={
          !editing ? (
            <Button type="primary" icon={<EditOutlined />} onClick={() => setEditing(true)}>
              编辑
            </Button>
          ) : null
        }
      >
        {!editing ? (
          <Descriptions column={1} bordered>
            <Descriptions.Item label={<><UserOutlined /> 用户名</>}>
              {user?.username}
            </Descriptions.Item>
            <Descriptions.Item label={<><IdcardOutlined /> 角色</>}>
              {getRoleTag(user?.role)}
            </Descriptions.Item>
            <Descriptions.Item label={<><MailOutlined /> 邮箱</>}>
              {user?.email || '未设置'}
            </Descriptions.Item>
            <Descriptions.Item label={<><PhoneOutlined /> 手机号</>}>
              {user?.phone || '未设置'}
            </Descriptions.Item>
            <Descriptions.Item label="个人背景">
              {user?.background || '未设置'}
            </Descriptions.Item>
          </Descriptions>
        ) : (
          <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
          >
            <Form.Item label="用户名">
              <Input prefix={<UserOutlined />} value={user?.username} disabled />
            </Form.Item>
            
            <Form.Item label="角色">
              <Input value={user?.role === 'ADMIN' ? '管理员' : '普通用户'} disabled />
            </Form.Item>
            
            <Form.Item 
              name="email" 
              label="邮箱"
              rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
            >
              <Input prefix={<MailOutlined />} placeholder="请输入邮箱" />
            </Form.Item>
            
            <Form.Item label="手机号">
              <Input prefix={<PhoneOutlined />} placeholder="暂不支持修改" disabled />
            </Form.Item>
            
            <Form.Item name="background" label="个人背景">
              <Input.TextArea rows={4} placeholder="介绍一下你自己..." />
            </Form.Item>
            
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={saving} icon={<SaveOutlined />} style={{ marginRight: 8 }}>
                保存
              </Button>
              <Button onClick={handleCancel}>
                取消
              </Button>
            </Form.Item>
          </Form>
        )}
      </Card>
    </div>
  );
};

export default Profile;
