import api from './api'

export interface AuthUser {
  userId: string
  email: string
  fullName: string
}

export async function login(email: string, password: string): Promise<string> {
  const { data } = await api.post('/auth/login', { email, password })
  const { token, ...user } = data.data
  localStorage.setItem('auth_token', token)
  localStorage.setItem('auth_user', JSON.stringify(user))
  return token
}

export async function register(email: string, password: string, fullName: string): Promise<string> {
  const { data } = await api.post('/auth/register', { email, password, fullName })
  const { token, ...user } = data.data
  localStorage.setItem('auth_token', token)
  localStorage.setItem('auth_user', JSON.stringify(user))
  return token
}

export function logout() {
  localStorage.removeItem('auth_token')
  localStorage.removeItem('auth_user')
  window.location.href = '/auth/login'
}

export function getUser(): AuthUser | null {
  if (typeof window === 'undefined') return null
  const raw = localStorage.getItem('auth_user')
  return raw ? JSON.parse(raw) : null
}

export function isAuthenticated(): boolean {
  if (typeof window === 'undefined') return false
  return !!localStorage.getItem('auth_token')
}
