
import { useEffect, useState } from 'react'
import './Leaderboard.css'


    function Leaderboard({ refreshCount }) {
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchLeaderboard = async () => {
      try {
        const response = await fetch('/api/leaderboard', {
          credentials: 'same-origin',
        })

        if (!response.ok) {
          throw new Error('Failed to load leaderboard')
        }

        const data = await response.json()
        setStudents(data)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchLeaderboard()
 }, [refreshCount])

  return (
    <section className="dashboard-section">
      <div className="section-heading">
        <div>
          <h2>🏆 Top Helpers Leaderboard</h2>
          <p>Students ranked by successfully completed help requests.</p>
        </div>
      </div>

      {loading && <p>Loading leaderboard...</p>}

      {error && <p role="alert">{error}</p>}

      {!loading && !error && students.length === 0 && (
        <p>No students available yet.</p>
      )}

      {!loading && !error && students.length > 0 && (
        <div className="leaderboard-table-wrapper">
          <table className="leaderboard-table">
            <thead>
              <tr>
                <th>Rank</th>
                <th>Student</th>
                <th>Course</th>
                <th>Helps Completed</th>
              </tr>
            </thead>

            <tbody>
              {students.map((student, index) => (
                <tr key={student.roll}>
                  <td>
                    {index === 0
                      ? '🥇'
                      : index === 1
                        ? '🥈'
                        : index === 2
                          ? '🥉'
                          : `#${index + 1}`}
                  </td>

                  <td>
                    <strong>{student.name}</strong>
                    <span className="leaderboard-roll">
                      Roll {student.roll}
                    </span>
                  </td>

                  <td>{student.course}</td>

                  <td>
                    <strong>{student.helps_completed}</strong>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

export default Leaderboard
