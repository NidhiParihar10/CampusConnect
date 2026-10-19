
import { useEffect, useState } from 'react'
import Dashboard from './Dashboard'
import './App.css'

function App() {
  const [roll, setRoll] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [loggedInRoll, setLoggedInRoll] = useState(null)
  const [checkingSession, setCheckingSession] = useState(true)

  // Restore login session after page refresh
  useEffect(() => {
    const checkSession = async () => {
      try {
        const response = await fetch('/api/auth/me', {
          credentials: 'same-origin',
        })

        if (response.ok) {
          const data = await response.json()
          setLoggedInRoll(data.roll)
        }
      } catch (error) {
        console.error('Session check failed:', error)
      } finally {
        setCheckingSession(false)
      }
    }

    checkSession()
  }, [])

  // Student login
  const handleLogin = async (e) => {
    e.preventDefault()
    setMessage('')
    setLoading(true)

    try {
      const csrfResponse = await fetch('/api/auth/csrf', {
        credentials: 'same-origin',
      })

      if (!csrfResponse.ok) {
        throw new Error('Unable to get CSRF token')
      }

      const csrf = await csrfResponse.json()

      const response = await fetch('/api/auth/login', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/json',
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({
          roll: roll.trim(),
          password,
        }),
      })

      const result = await response.json()

      if (!response.ok) {
        setMessage(result.error || 'Login failed')
        return
      }

      setLoggedInRoll(result.roll)
      setPassword('')
      setMessage('')

    } catch (error) {
      console.error('Login error:', error)
      setMessage('Unable to connect to the server')
    } finally {
      setLoading(false)
    }
  }

  // Student logout
  const handleLogout = async () => {
    try {
      const csrfResponse = await fetch('/api/auth/csrf', {
        credentials: 'same-origin',
      })

      if (!csrfResponse.ok) {
        throw new Error('Failed to get CSRF token')
      }

      const csrf = await csrfResponse.json()

      const response = await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          [csrf.headerName]: csrf.token,
        },
      })

      if (!response.ok) {
        throw new Error('Logout failed')
      }

      setLoggedInRoll(null)
      setRoll('')
      setPassword('')
      setMessage('Logged out successfully!')

    } catch (error) {
      console.error('Logout failed:', error)
      alert('Logout failed. Please try again.')
    }
  }

  // Show loading screen while checking session
  if (checkingSession) {
    return (
      <div className="login-page">
        <h2 style={{ color: 'white' }}>
          Checking your session...
        </h2>
      </div>
    )
  }

  // Show Dashboard if student is logged in
  if (loggedInRoll) {
    return (
      <Dashboard
        roll={loggedInRoll}
        onLogout={handleLogout}
      />
    )
  }

  // Otherwise show Login Page
  return (
    <div className="login-page">
      <div className="login-card">

        <div className="logo">🎓</div>

        <h1>CampusConnect</h1>

        <p className="subtitle">
          Connect. Collaborate. Learn Together.
        </p>

        <h2>Welcome Back!</h2>

        <p className="login-text">
          Login to your student account
        </p>

        <form onSubmit={handleLogin}>

          <label htmlFor="roll">Roll Number</label>
          <input
            id="roll"
            type="text"
            placeholder="Enter your roll number"
            value={roll}
            onChange={(e) => setRoll(e.target.value)}
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>

        </form>

        {message && (
          <p role="status" className="login-message">
            {message}
          </p>
        )}

        <p className="footer-text">
          CampusConnect — Students Helping Students
        </p>

      </div>
    </div>
  )
}

export default App
