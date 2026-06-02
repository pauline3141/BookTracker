import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import client from '../api/client'
import ReadingProgress from '../components/ReadingProgress.jsx'

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
            <nav>
                <Link to="/">Meine Regale</Link>
                <Link to="/search">Bücher suchen</Link>
            </nav>
            <div className="container">
                <h1>{shelf?.name}</h1>
                <p>{shelf?.description}</p>
                {entries.map(entry => (
                    <div key={entry.id} className="card">
                        <h2>{entry.book.title}</h2>
                        <p>{entry.book.author}</p>
                        <ReadingProgress entry={entry} onUpdate={updated =>
                            setEntries(entries.map(e => e.id === updated.id ? updated : e))
                        } />
                    </div>
                ))}
            </div>
        </div>
    )
}