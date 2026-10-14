import { useEffect, useState } from 'react'
import UrlForm from './components/UrlForm'
import UrlList from './components/UrlList'
import { fetchRecent, deleteUrl } from './api'

export default function App() {
  const [urls, setUrls] = useState([])
  const [latest, setLatest] = useState(null)

  const load = async () => {
    try {
      setUrls(await fetchRecent())
    } catch {
      setUrls([])
    }
  }

  useEffect(() => { load() }, [])

  const handleCreated = (item) => {
    setLatest(item)
    load()
  }

  const handleDelete = async (code) => {
    await deleteUrl(code)
    load()
  }

  return (
    <main className="container">
      <h1>URL Shortener</h1>
      <p className="subtitle">Turn long links into short, shareable ones.</p>
      <UrlForm onCreated={handleCreated} />
      {latest && (
        <div className="result">
          <span>Your short link:</span>
          <a href={latest.shortUrl} target="_blank" rel="noreferrer">{latest.shortUrl}</a>
          <button onClick={() => navigator.clipboard.writeText(latest.shortUrl)}>Copy</button>
        </div>
      )}
      <UrlList urls={urls} onDelete={handleDelete} onRefresh={load} />
    </main>
  )
}
