
import { useEffect, useState } from 'react'
import './MySkills.css'

function MySkills() {
  const [skills, setSkills] = useState([])
  const [newSkill, setNewSkill] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadSkills = async () => {
    setLoading(true)
    setError('')

    try {
      const response = await fetch('/api/skills/me', {
        credentials: 'same-origin',
      })

      if (!response.ok) {
        throw new Error('Failed to load your skills')
      }

      const data = await response.json()
      setSkills(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadSkills()
  }, [])

  const handleAddSkill = async (event) => {
    event.preventDefault()

    const skill = newSkill.trim()

    if (!skill) {
      setError('Please enter a skill')
      return
    }

    if (skill.length > 50) {
      setError('Skill must be 50 characters or less')
      return
    }

    setSaving(true)
    setError('')
    setMessage('')

    try {
      const csrfResponse = await fetch('/api/auth/csrf', {
        credentials: 'same-origin',
      })

      if (!csrfResponse.ok) {
        throw new Error('Unable to get CSRF token')
      }

      const csrf = await csrfResponse.json()

      const response = await fetch('/api/skills/me', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/json',
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({ skill }),
      })

      const result = await response.json()

      if (!response.ok) {
        throw new Error(result.error || 'Failed to add skill')
      }

      setMessage('Skill added successfully!')
      setNewSkill('')
      await loadSkills()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="dashboard-section">
      <div className="section-heading">
        <div>
          <h2>🧠 My Skills</h2>
          <p>Add skills to help other students find you.</p>
        </div>
      </div>

      {loading ? (
        <p>Loading skills...</p>
      ) : (
        <div className="my-skills-list">
          {skills.length === 0 ? (
            <p>No skills added yet.</p>
          ) : (
            skills.map((skill) => (
              <span className="skill-tag" key={skill}>
                {skill}
              </span>
            ))
          )}
        </div>
      )}

      <form className="my-skills-form" onSubmit={handleAddSkill}>
        <input
          type="text"
          value={newSkill}
          onChange={(event) => setNewSkill(event.target.value)}
          placeholder="Enter a skill (e.g. Java)"
          maxLength={50}
          disabled={saving}
          required
        />

        <button type="submit" disabled={saving}>
          {saving ? 'Adding...' : '+ Add Skill'}
        </button>
      </form>

      {error && <p className="skills-error" role="alert">{error}</p>}
      {message && <p className="skills-success">{message}</p>}
    </section>
  )
}

export default MySkills
