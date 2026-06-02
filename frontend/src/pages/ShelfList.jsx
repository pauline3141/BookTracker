import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import client from '../api/client'

export default function ShelfList() {
    const [shelves, setShelves] = useState([])
    const navigate = useNavigate()

    useEffect(() => {
        client.get('/shelves').then(res => setShelves(res.data))
    }, [])

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1>Meine Regale</h1>
                {shelves.map(shelf => (
                    <div
                        key={shelf.id}
                        className="card card-clickable"
                        onClick={() => navigate(`/shelves/${shelf.id}`)}
                    >
                        <h2>{shelf.name}</h2>
                        <p style={{ color: '#666', fontSize: '0.9rem' }}>{shelf.description}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}