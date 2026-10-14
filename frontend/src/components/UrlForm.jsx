import { useState } from 'react'
import { shortenUrl } from '../api'

export default function UrlForm({ onCreated }) {
  const [url, setUrl] = useState('')
  const [alias, setAlias] = useState('')
  const [days, setDays] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await shortenUrl({
        url,
        customAlias: alias || null,
        expiryDays: days ? Number(days) : null,
      })
      onCreated(data)
      setUrl(''); setAlias(''); setDays('')
    } catch (err) {
      setError(err.response?.data?.error || 'Could not reach the server')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Paste your long URL here"
        value={url}
        onChange={(e) => setUrl(e.target.value)}
        required
      />
      <div className="row">
        <input
          type="text"
          placeholder="Custom alias (optional)"
          value={alias}
          onChange={(e) => setAlias(e.target.value)}
        />
        <input
          type="number"
          min="1"
          max="365"
          placeholder="Expiry in days (optional)"
          value={days}
          onChange={(e) => setDays(e.target.value)}
        />
      </div>
      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? 'Shortening...' : 'Shorten URL'}
      </button>
    </form>
  )
}
