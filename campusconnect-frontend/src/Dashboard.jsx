import { useEffect, useState } from 'react'
import CreateRequest from './CreateRequest'
import Leaderboard from './Leaderboard'
import MySkills from './MySkills'
import './Dashboard.css'
import MatchingHelpers from './MatchingHelpers'

function Dashboard({ roll, onLogout }) {
  const [requests, setRequests] = useState([])
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refreshCount, setRefreshCount] = useState(0)
  const [processingId, setProcessingId] = useState(null)

  // Load all help requests
  useEffect(() => {
    let cancelled = false

    const fetchRequests = async () => {
      setLoading(true)
      setError('')

      try {
        const response = await fetch('/api/requests', {
          credentials: 'same-origin',
        })

        if (!response.ok) {
          throw new Error('Failed to load help requests')
        }

        const data = await response.json()

        if (!cancelled) {
          setRequests(data)
        }
      } catch (err) {
        if (!cancelled) {
          setError(err.message)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    fetchRequests()

    return () => {
      cancelled = true
    }
  }, [refreshCount])

  // Load logged-in student profile
  useEffect(() => {
    let cancelled = false

    const fetchProfile = async () => {
      try {
        const response = await fetch('/api/students/me', {
          credentials: 'same-origin',
        })

        if (!response.ok) {
          throw new Error('Failed to load student profile')
        }

        const data = await response.json()

        if (!cancelled) {
          setProfile(data)
        }
      } catch (err) {
        console.error('Profile error:', err)
      }
    }

    fetchProfile()

    return () => {
      cancelled = true
    }
  }, [roll])

  // Refresh dashboard data
  const refreshRequests = () => {
    setRefreshCount((count) => count + 1)
  }

  // Common API handler: Accept, Start and Resolve
  const performRequestAction = async (requestId, action) => {
    if (processingId !== null) return

    setProcessingId(requestId)

    try {
      const csrfResponse = await fetch('/api/auth/csrf', {
        credentials: 'same-origin',
      })

      if (!csrfResponse.ok) {
        throw new Error('Unable to get CSRF token')
      }

      const csrf = await csrfResponse.json()

      const response = await fetch(
        `/api/requests/${requestId}/${action}`,
        {
          method: 'POST',
          credentials: 'same-origin',
          headers: {
            [csrf.headerName]: csrf.token,
          },
        }
      )

      const result = await response.json()

      if (!response.ok) {
        alert(result.error || 'Request action failed')
        refreshRequests()
        return
      }

      alert(result.message || 'Action completed successfully!')
      refreshRequests()
    } catch (err) {
      console.error('Request action error:', err)
      alert('Unable to complete action. Please try again.')
    } finally {
      setProcessingId(null)
    }
  }

  const handleAcceptRequest = (requestId) => {
    performRequestAction(requestId, 'accept')
  }

  const handleStartRequest = (requestId) => {
    performRequestAction(requestId, 'start')
  }

  const handleResolveRequest = (requestId) => {
    performRequestAction(requestId, 'resolve')
  }

  // Requests created by logged-in student
  const myHelpRequests = requests.filter(
    (request) => String(request.roll) === String(roll)
  )

  const myHelpHistory = requests.filter(
  (request) =>
    String(request.assigned_helper) === String(roll) &&
    request.status === 'RESOLVED'
)

  // Requests assigned to logged-in student
  const myAssignedRequests = requests.filter(
    (request) =>
      String(request.assigned_helper) === String(roll) &&
      ['ASSIGNED', 'IN_PROGRESS'].includes(request.status)
  )
  const totalHelpsCompleted = myHelpHistory.length

  const openRequests = requests.filter(
    (request) => request.status === 'OPEN'
  ).length

  // Buttons for request status transitions
  const renderRequestActions = (request) => {
    const isOwner = String(request.roll) === String(roll)
    const isAssignedHelper =
      request.assigned_helper != null &&
      String(request.assigned_helper) === String(roll)

    if (request.status === 'OPEN' && !isOwner) {
      return (
        <button
          type="button"
          className="accept-request-btn"
          disabled={processingId !== null}
          onClick={() => handleAcceptRequest(request.request_id)}
        >
          {processingId === request.request_id
            ? 'Processing...'
            : '🤝 Accept Request'}
        </button>
      )
    }

    if (request.status === 'ASSIGNED' && isAssignedHelper) {
      return (
        <button
          type="button"
          className="start-request-btn"
          disabled={processingId !== null}
          onClick={() => handleStartRequest(request.request_id)}
        >
          {processingId === request.request_id
            ? 'Processing...'
            : '▶ Start Request'}
        </button>
      )
    }

    if (request.status === 'IN_PROGRESS' && isAssignedHelper) {
      return (
        <button
          type="button"
          className="resolve-request-btn"
          disabled={processingId !== null}
          onClick={() => handleResolveRequest(request.request_id)}
        >
          {processingId === request.request_id
            ? 'Processing...'
            : '✅ Resolve Request'}
        </button>
      )
    }

    return null
  }

  // Reusable request card
  const renderRequestCard = (request, showActions = false) => (
    <article className="request-card" key={request.request_id}>
      <div className="request-card-top">
        <span className="request-id">
          #{request.request_id}
        </span>

        <span
          className={`status-badge status-${request.status.toLowerCase()}`}
        >
          {request.status}
        </span>
      </div>

      <h3>{request.topic}</h3>
      <p>{request.description}</p>

      
<div className="request-footer">
  <span>👤 Student {request.roll}</span>
</div>

{showActions && (
  <>
    {renderRequestActions(request)}

    {request.status === 'OPEN' && (
      <MatchingHelpers requestId={request.request_id} />
    )}
  </>
)}

    </article>
  )

  return (
    <div className="dashboard-page">

      {/* Navigation */}
      <header className="dashboard-navbar">
        <div className="dashboard-brand">
          <span>🎓</span>
          <strong>CampusConnect</strong>
        </div>

        <div className="dashboard-user">
          <span>Student {roll}</span>

          {onLogout && (
            <button
              type="button"
              className="dashboard-logout"
              onClick={onLogout}
            >
              Logout
            </button>
          )}
        </div>
      </header>

      <main className="dashboard-container">

        {/* Student Profile */}
        <section className="dashboard-section">
          <h2>👤 My Profile</h2>

          {profile ? (
            <div className="student-profile">
              <div>
                <h3>{profile.name}</h3>
                <p>🎓 {profile.course} Student</p>
              </div>

              <div>
                <p>
                  <strong>Roll Number:</strong> {profile.roll}
                </p>
                <p>
                  <strong>Course:</strong> {profile.course}
                </p>
              </div>
            </div>
          ) : (
            <p>Loading student profile...</p>
          )}
          

        </section>
{/* Student Skills */}
<MySkills />
        {/* Welcome Banner */}
        <section className="dashboard-welcome">
          <h1>
            Welcome back, {profile?.name || `Student ${roll}`}! 👋
          </h1>
          <p>
            Connect with your campus community,
            share knowledge and help each other grow.
          </p>
        </section>

        {/* Statistics */}
        <section className="dashboard-stats">
          <div className="stat-card">
            <span>📋 Total Requests</span>
            <h2>{requests.length}</h2>
          </div>

          <div className="stat-card">
            <span>🟢 Open Requests</span>
            <h2>{openRequests}</h2>
          </div>

          <div className="stat-card">
            <span>🙋 My Requests</span>
            <h2>{myHelpRequests.length}</h2>
          </div>
          <div className="stat-card">
    <span>🏆 Helps Completed</span>
    <h2>{totalHelpsCompleted}</h2>
  </div>
        </section>

        {/* My Help Requests */}
        <section className="dashboard-section">
          <div className="section-heading">
            <div>
              <h2>🙋 My Help Requests</h2>
              <p>Requests created by you.</p>
            </div>
          </div>

          {loading && <p>Loading your requests...</p>}

          {error && <p role="alert">{error}</p>}

          {!loading && !error && myHelpRequests.length === 0 && (
            <p>You haven't created any help requests yet.</p>
          )}

          {!loading && !error && (
            <div className="requests-grid">
              {myHelpRequests.map((request) =>
                renderRequestCard(request)
              )}
            </div>
          )}
        </section>

        {/* My Assigned Requests */}
        <section className="dashboard-section">
          <div className="section-heading">
            <div>
              <h2>🤝 My Assigned Requests</h2>
              <p>Requests you accepted and are working on.</p>
            </div>
          </div>

          {loading && <p>Loading assigned requests...</p>}

          {!loading && !error && myAssignedRequests.length === 0 && (
            <p>No active requests assigned to you.</p>
          )}

          {!loading && !error && (
            <div className="requests-grid">
              {myAssignedRequests.map((request) =>
                renderRequestCard(request, true)
              )}
            </div>
          )}
        </section>


{/* My Help History */}
<section className="dashboard-section">
  <div className="section-heading">
    <div>
      <h2>🏆 My Help History</h2>
      <p>Requests you successfully resolved.</p>
    </div>
  </div>

  {loading && <p>Loading help history...</p>}

  {!loading && !error && myHelpHistory.length === 0 && (
    <p>You haven't resolved any help requests yet.</p>
  )}

  {!loading && !error && myHelpHistory.length > 0 && (
    <div className="requests-grid">
      {myHelpHistory.map((request) =>
        renderRequestCard(request)
      )}
    </div>
  )}
</section>
{/* Student Leaderboard */}
<Leaderboard refreshCount={refreshCount} />

        {/* Create Help Request */}
        <section className="dashboard-section">
          <CreateRequest onRequestCreated={refreshRequests} />
        </section>

        {/* Available Help Requests */}
        <section className="dashboard-section">
          <div className="section-heading">
            <div>
              <h2>📋 Available Help Requests</h2>
              <p>Explore questions from your campus community.</p>
            </div>

            <button
              type="button"
              className="refresh-button"
              onClick={refreshRequests}
            >
              ↻ Refresh
            </button>
          </div>

          {loading && <p>Loading requests...</p>}

          {error && <p role="alert">{error}</p>}

          {!loading && !error && requests.length === 0 && (
            <p>No help requests available.</p>
          )}

          {!loading && !error && (
            <div className="requests-grid">
              {requests.map((request) =>
                renderRequestCard(request, true)
              )}
            </div>
          )}
        </section>

      </main>
    </div>
  )
}

export default Dashboard
