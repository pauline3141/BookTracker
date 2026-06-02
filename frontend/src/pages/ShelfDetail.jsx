import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import ReadingProgress from '../components/ReadingProgress.jsx'
import client from '../api/client'

export default function ShelfDetail() {
    const { id } = useParams()
    const [shelf, setShelf] = useState(null)
    const [entries, setEntries] = useState([])

    useEffect(() => {
        client.get(`/shelves/${id}`).then(res => setShelf(res.data))
        client.get(`/shelves/${id}/entries`).then(res => setEntries(res.data))
    }, [id])

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1>{shelf?.name}</h1>
                {shelf?.description && (
                    <p style={{ color: '#666', marginBottom: '1.5rem' }}>{shelf.description}</p>
                )}
                {entries.length === 0 && (
                    <p style={{ color: '#999' }}>Noch keine Bücher in diesem Regal.</p>
                )}
                {entries.map(entry => (
                    <div key={entry.id} className="card book-card">
                        {entry.book.coverUrl ? (
                            <img src={entry.book.coverUrl} alt={entry.book.title} className="book-cover" />
                        ) : (
                            <div className="book-cover-placeholder">Kein Cover</div>
                        )}
                        <div style={{ flex: 1 }}>
                            <h2>{entry.book.title}</h2>
                            <p style={{ color: '#666', fontSize: '0.9rem' }}>{entry.book.author}</p>
                            <ReadingProgress entry={entry} onUpdate={updated =>
                                setEntries(entries.map(e => e.id === updated.id ? updated : e))
                            } />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}