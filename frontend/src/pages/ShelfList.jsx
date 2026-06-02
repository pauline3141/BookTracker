import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client'

export default function ShelfList() {
    const [shelves, setShelves] = useState([])

    useEffect(() => {
        client.get('/shelves').then(res => setShelves(res.data))
    }, [])

    return (
        <div>
            <nav>
                <Link to="/">Meine Regale</Link>
                <Link to="/search">Bücher suchen</Link>
            </nav>
            <div className="container">
                <h1>Meine Regale</h1>
                {shelves.map(shelf => (
                    <div key={shelf.id} className="card">
                        <h2>{shelf.name}</h2>
                        <p>{shelf.description}</p>
                        <Link to={`/shelves/${shelf.id}`}>
                            <button>Öffnen</button>
                        </Link>
                    </div>
                ))}
            </div>
        </div>
    )
}