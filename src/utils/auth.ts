// 认证工具函数

// Token 存储键名
const TOKEN_KEY = 'auth_token'
const TOKEN_EXPIRY_KEY = 'auth_token_expiry'
const USERNAME_KEY = 'auth_username'

// Token 有效期（毫秒）- 24 小时
const TOKEN_EXPIRY_MS = 24 * 60 * 60 * 1000

/**
 * 保存登录信息
 */
export const saveAuth = (token: string, username: string, expiresIn?: number) => {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USERNAME_KEY, username)
  
  // 计算过期时间
  const expiry = expiresIn || TOKEN_EXPIRY_MS
  const expiryTime = Date.now() + expiry
  localStorage.setItem(TOKEN_EXPIRY_KEY, expiryTime.toString())
}

/**
 * 获取 Token
 */
export const getToken = (): string | null => {
  if (!isTokenValid()) {
    return null
  }
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 获取用户名
 */
export const getUsername = (): string | null => {
  return localStorage.getItem(USERNAME_KEY)
}

/**
 * 检查 Token 是否有效
 */
export const isTokenValid = (): boolean => {
  const token = localStorage.getItem(TOKEN_KEY)
  const expiryStr = localStorage.getItem(TOKEN_EXPIRY_KEY)
  
  if (!token) {
    return false
  }
  
  if (!expiryStr) {
    // 没有过期时间，默认有效（兼容旧数据）
    return true
  }
  
  const expiry = parseInt(expiryStr, 10)
  if (isNaN(expiry)) {
    return true
  }
  
  // 检查是否过期
  return Date.now() < expiry
}

/**
 * 检查用户是否已登录
 */
export const isAuthenticated = (): boolean => {
  return isTokenValid()
}

/**
 * 清除登录信息（退出登录）
 */
export const clearAuth = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USERNAME_KEY)
  localStorage.removeItem(TOKEN_EXPIRY_KEY)
}

/**
 * 获取剩余有效时间（毫秒）
 */
export const getTokenRemainingTime = (): number | null => {
  const expiryStr = localStorage.getItem(TOKEN_EXPIRY_KEY)
  if (!expiryStr) {
    return null
  }
  
  const expiry = parseInt(expiryStr, 10)
  if (isNaN(expiry)) {
    return null
  }
  
  return Math.max(0, expiry - Date.now())
}
