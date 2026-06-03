import { useState } from 'react'
import { addNote } from '../api/bookTrackerApi'
import type { BookNote } from '../types'

type NoteFormProps = {
    entryId: number
    onAdd: (note: BookNote) => void
}

export default function NoteForm({ entryId, onAdd }: NoteFormProps) {
    const [content, setContent] = useState('')
    const [pageReference, setPageReference] = useState('')
    const [error, setError] = useState<string | null>(null)

    const submit = () => {
        if (!content.trim()) return
        addNote(entryId, {
            content,
            pageReference: pageReference ? Number(pageReference) : null,
            isPublic: false
        })
            .then(note => {
                onAdd(note)
                setContent('')
                setPageReference('')
                setError(null)
            })
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
    }

    return (
        <div style={{ marginTop: '1rem' }}>
            <h3>Notiz hinzufügen</h3>
            {error && <p className="error">Fehler: {error}</p>}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <input
                    type="number"
                    value={pageReference}
                    onChange={e => setPageReference(e.target.value)}
                    placeholder="Seite (optional)"
                />
                <textarea
                    value={content}
                    onChange={e => setContent(e.target.value)}
                    placeholder="Notiz..."
                    rows={3}
                />
                <button onClick={submit}>Hinzufügen</button>
            </div>
        </div>
    )
}