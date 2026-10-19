
import { useEffect, useState } from 'react'
import Dashboard from './Dashboard'
import './App.css'

function App() {
  const [roll, setRoll] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [course, setCourse] = useState('')
  const [mode, setMode] = useState('login')
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

  // Get CSRF token for secure POST requests
  const getCsrfToken = async () => {
    const response = await fetch('/api/auth/csrf', {
      credentials: 'same-origin',
    })

    if (!response.ok) {
      throw new Error('Unable to get security token')
    }

    return response.json()
  }

  // Student login
  const handleLogin = async (e) => {
    e.preventDefault()
    setMessage('')
    setLoading(true)

    try {
      const csrf = await getCsrfToken()

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

  // Register a new student
  const handleRegister = async (e) => {
    e.preventDefault()
    setMessage('')
    setLoading(true)

    try {
      const csrf = await getCsrfToken()

      const response = await fetch('/api/auth/register', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/json',
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({
          name: name.trim(),
          course: course.trim(),
          roll: roll.trim(),
          password,
        }),
      })

      const result = await response.json()

      if (!response.ok) {
        setMessage(result.error || 'Registration failed')
        return
      }

      setMode('login')
      setName('')
      setCourse('')
      setPassword('')
      setMessage('Registration successful! Please log in.')
    } catch (error) {
      console.error('Registration error:', error)
      setMessage('Unable to connect to the server')
    } finally {
      setLoading(false)
    }
  }

  // Student logout
  const handleLogout = async () => {
    try {
      const csrf = await getCsrfToken()

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

  if (checkingSession) {
    return (
      <div className="login-page">
        <h2 style={{ color: 'white' }}>
          Checking your session...
        </h2>
      </div>
    )
  }

  if (loggedInRoll) {
    return (
      <Dashboard
        roll={loggedInRoll}
        onLogout={handleLogout}
      />
    )
  }

  return (
    <div className="login-page">
      <div className="login-card">

        <div className="logo">🎓</div>

        <h1>CampusConnect</h1>

        <p className="subtitle">
          Connect. Collaborate. Learn Together.
        </p>

        <div className="mode-switch">
          <button
            type="button"
            className={mode === 'login' ? 'mode-button active' : 'mode-button'}
            onClick={() => {
              setMode('login')
              setMessage('')
            }}
          >
            Login
          </button>

          <button
            type="button"
            className={mode === 'register' ? 'mode-button active' : 'mode-button'}
            onClick={() => {
              setMode('register')
              setMessage('')
            }}
          >
            Register
          </button>
        </div>

        <h2>
          {mode === 'login' ? 'Welcome Back!' : 'Create Account'}
        </h2>

        <p className="login-text">
          {mode === 'login'
            ? 'Login to your student account'
            : 'Register for your student account'}
        </p>

        <form onSubmit={mode === 'login' ? handleLogin : handleRegister}>

          {mode === 'register' && (
            <>
              <label htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                placeholder="Enter your full name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                maxLength={100}
                required
              />

              <label htmlFor="course">Course</label>
              <input
                id="course"
                type="text"
                placeholder="e.g. MCA"
                value={course}
                onChange={(e) => setCourse(e.target.value)}
                maxLength={100}
                required
              />
            </>
          )}

          <label htmlFor="roll">Roll Number</label>
          <input
            id="roll"
            type="text"
            placeholder="Enter your roll number"
            value={roll}
            onChange={(e) => setRoll(e.target.value)}
            maxLength={50}
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder={
              mode === 'register'
                ? 'At least 8 characters'
                : 'Enter your password'
            }
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={mode === 'register' ? 8 : undefined}
            required
          />

          <button type="submit" disabled={loading}>
            {loading
              ? 'Please wait...'
              : mode === 'login'
                ? 'Login'
                : 'Create Account'}
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
