import { useState } from 'react'
import client from '../api/client'

export default function ReadingProgress({ entry, onUpdate }) {
    const [currentPage, setCurrentPage] = useState(entry.currentPage ?? 0)
    const [totalPages, setTotalPages] = useState(
        entry.totalPages > 0 ? entry.totalPages : (entry.book?.totalPages ?? 0)
    )

    const save = () => {
        client.patch(`/shelves/${entry.shelfId}/entries/${entry.id}/progress`, {
            currentPage,
            totalPages
        }).then(res => onUpdate(res.data))
    }

    const handleCurrentPage = (value) => {
        const val = Math.max(0, Math.min(Number(value), totalPages))
        setCurrentPage(val)
    }

    const handleTotalPages = (value) => {
        const val = Math.max(0, Number(value))
        setTotalPages(val)
        if (currentPage > val) setCurrentPage(val)
    }

    const percent = totalPages > 0 ? Math.round((currentPage / totalPages) * 100) : 0

    const sliderStyle = {
        flex: 1,
        background: `linear-gradient(to right, #3d2b1f ${percent}%, #ddd ${percent}%)`
    }

    return (
        <div style={{ marginTop: '0.75rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                <input
                    type="range"
                    min={0}
                    max={totalPages || 100}
                    value={currentPage}
                    onChange={e => handleCurrentPage(e.target.value)}
                    style={sliderStyle}
                />
                <span style={{ fontSize: '0.85rem', color: '#666', whiteSpace: 'nowrap' }}>
          {percent}%
        </span>
            </div>
            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <input
                    type="number"
                    value={currentPage}
                    onChange={e => handleCurrentPage(e.target.value)}
                    style={{ width: '80px' }}
                    placeholder="Seite"
                />
                <span style={{ color: '#666' }}>/</span>
                <input
                    type="number"
                    value={totalPages}
                    onChange={e => handleTotalPages(e.target.value)}
                    style={{ width: '80px' }}
                    placeholder="Gesamt"
                />
                <button onClick={save}>Speichern</button>
            </div>
        </div>
    )
}