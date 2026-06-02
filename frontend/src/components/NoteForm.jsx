import { useState } from 'react'
import client from '../api/client'

export default function NoteForm({ entryId, onAdd }) {
    const [content, setContent] = useState('')
    const [pageReference, setPageReference] = useState('')

    const submit = () => {
        client.post(`/entries/${entryId}/notes`, {
            content,
            pageReference: pageReference ? Number(pageReference) : null,
            isPublic: false
        }).then(res => {
            onAdd(res.data)
            setContent('')
            setPageReference('')
        })
    }

    return (
        <div style={{ marginTop: '1rem' }}>
            <h3>Notiz hinzufügen</h3>
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