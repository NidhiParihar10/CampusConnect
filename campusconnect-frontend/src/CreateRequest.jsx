
import { useState } from 'react'

function CreateRequest({ onRequestCreated }) {
  const [topic, setTopic] = useState('')
  const [description, setDescription] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage('')
    setLoading(true)

    try {
      // Get a CSRF token for the current session
      const csrfResponse = await fetch('/api/auth/csrf', {
        credentials: 'same-origin',
      })

      if (!csrfResponse.ok) {
        throw new Error('Unable to get CSRF token')
      }

      const csrf = await csrfResponse.json()

      // Create request using authenticated session
      const response = await fetch('/api/requests', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/json',
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({
          topic: topic.trim(),
          description: description.trim(),
        }),
      })

      const result = await response.json()

      if (!response.ok) {
        setMessage(result.error || 'Unable to create request')
        return
      }

      setMessage('Help request created successfully!')
      setTopic('')
      setDescription('')

      if (onRequestCreated) {
        onRequestCreated()
      }
    } catch (error) {
      console.error('Create request failed:', error)
      setMessage('Could not connect to the server')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="create-request">
      <h3>➕ Create New Help Request</h3>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="topic">Topic</label>
          <input
            id="topic"
            type="text"
            placeholder="e.g. Python Functions"
            value={topic}
            onChange={(e) => setTopic(e.target.value)}
            maxLength={100}
            required
          />
        </div>

        <div>
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            placeholder="Describe what you need help with"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Submitting...' : 'Create Request'}
        </button>
      </form>

      {message && <p role="status">{message}</p>}
    </section>
  )
}

export default CreateRequest
