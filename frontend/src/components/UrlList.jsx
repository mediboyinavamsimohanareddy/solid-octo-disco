export default function UrlList({ urls, onDelete, onRefresh }) {
  if (!urls.length) {
    return <p className="empty">No links yet. Create your first short URL above.</p>
  }
  return (
    <section className="card">
      <div className="list-head">
        <h2>Recent links</h2>
        <button onClick={onRefresh}>Refresh</button>
      </div>
      <table>
        <thead>
          <tr>
            <th>Short URL</th>
            <th>Original URL</th>
            <th>Clicks</th>
            <th>Expires</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {urls.map((u) => (
            <tr key={u.shortCode}>
              <td><a href={u.shortUrl} target="_blank" rel="noreferrer">{u.shortCode}</a></td>
              <td className="truncate" title={u.originalUrl}>{u.originalUrl}</td>
              <td>{u.clickCount}</td>
              <td>{u.expiresAt ? new Date(u.expiresAt).toLocaleDateString() : 'Never'}</td>
              <td><button onClick={() => onDelete(u.shortCode)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}
