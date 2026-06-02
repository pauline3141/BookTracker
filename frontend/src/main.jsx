import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './index.css'
import ShelfList from './pages/ShelfList.jsx'
import ShelfDetail from './pages/ShelfDetail.jsx'
import BookSearch from './pages/BookSearch.jsx'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ShelfList />} />
                <Route path="/shelves/:id" element={<ShelfDetail />} />
                <Route path="/search" element={<BookSearch />} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)