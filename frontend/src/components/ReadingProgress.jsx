import { useState } from 'react'
import client from '../api/client'

export default function ReadingProgress({ entry, onUpdate }) {
    const [currentPage, setCurrentPage] = useState(entry.currentPage ?? 0)
    const [totalPages, setTotalPages] = useState(entry.totalPages ?? 0)

    const save = () => {
        client.patch(`/shelves/${entry.shelf.id}/entries/${entry.id}/progress`, {
            currentPage,
            totalPages
        }).then(res => onUpdate(res.data))
    }

    return (
        <div style={{ marginTop: '0.5rem' }}>
            <p>Status: {entry.status}</p>
            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginTop: '0.5rem' }}>
                <input
                    type="number"
                    value={currentPage}
                    onChange={e => setCurrentPage(Number(e.target.value))}
                    style={{ width: '80px' }}
                />
                <span>/</span>
                <input
                    type="number"
                    value={totalPages}
                    onChange={e => setTotalPages(Number(e.target.value))}
                    style={{ width: '80px' }}
                />
                <button onClick={save}>Speichern</button>
            </div>
        </div>
    )
}