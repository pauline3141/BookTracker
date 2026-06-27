type ConfirmDialogProps = {
    title: string
    message: string
    confirmLabel?: string
    cancelLabel?: string
    onConfirm: () => void
    onCancel: () => void
}

export default function ConfirmDialog({
                                          title,
                                          message,
                                          confirmLabel = 'Löschen',
                                          cancelLabel = 'Abbrechen',
                                          onConfirm,
                                          onCancel
                                      }: ConfirmDialogProps) {
    return (
        <div
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background: 'rgba(0, 0, 0, 0.5)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 1000
            }}
            onClick={onCancel}
        >
            <div
                className="card"
                style={{ maxWidth: '400px', width: '90%' }}
                onClick={e => e.stopPropagation()}
            >
                <h2 style={{ marginTop: 0 }}>{title}</h2>
                <p style={{ color: '#666' }}>{message}</p>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                    <button className="secondary" onClick={onCancel}>
                        {cancelLabel}
                    </button>
                    <button
                        onClick={onConfirm}
                        style={{ background: '#dc2626', color: 'white' }}
                    >
                        {confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    )
}