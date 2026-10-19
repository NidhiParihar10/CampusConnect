
import { useState } from 'react'
import './MatchingHelpers.css'

function MatchingHelpers({ requestId }) {
  const [helpers, setHelpers] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [searched, setSearched] = useState(false)

  const findHelpers = async () => {
    setLoading(true)
    setError('')
    setSearched(false)
    setHelpers([])

    try {
      const response = await fetch(
        `/api/requests/${requestId}/matching-helpers`,
        {
          credentials: 'same-origin',
        }
      )

      if (!response.ok) {
        if (response.status === 403) {
          throw new Error('Please log in again to find helpers')
        }

        throw new Error('Failed to find matching helpers')
      }

      const data = await response.json()

      setHelpers(data.helpers || [])
      setSearched(true)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="matching-helpers">
      <button
        type="button"
        className="matching-helpers-btn"
        onClick={findHelpers}
        disabled={loading}
      >
        {loading ? 'Searching...' : '🔎 Find Matching Helpers'}
      </button>

      {error && (
        <p className="matching-error" role="alert">
          {error}
        </p>
      )}

      {searched && (
        <div className="matching-results">
          <h4>🏆 Suggested Helpers ({helpers.length})</h4>

          {helpers.length === 0 ? (
            <p>No matching helpers found.</p>
          ) : (
            <div className="matching-helper-list">
              {helpers.map((helper, index) => (
                <div
                  className="matching-helper-card"
                  key={helper.roll}
                >
                  <div className="matching-helper-header">
                    <strong>
                      #{index + 1} — {helper.name}
                    </strong>

                    <span className="matching-score">
                      Score: {helper.score}
                    </span>
                  </div>

                  <p>Roll: {helper.roll}</p>
                  <p>Course: {helper.course}</p>

                  <div className="matching-skills">
                    {(helper.matchedSkills || []).map((skill) => (
                      <span
                        className="matching-skill-tag"
                        key={skill}
                      >
                        ✓ {skill}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default MatchingHelpers
