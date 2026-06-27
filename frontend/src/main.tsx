import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import './index.css'
import DiscoverPage from './pages/DiscoverPage'
import ShelfList from './pages/ShelfList'
import ShelfDetail from './pages/ShelfDetail'
import BookSearch from './pages/BookSearch'
import BookDetail from './pages/BookDetail'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'

function PrivateRoute({ children }: { children: React.ReactNode }) {
    const token = localStorage.getItem('token')
    return token ? <>{children}</> : <Navigate to="/login" />
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/" element={<DiscoverPage />} />
                <Route path="/search" element={<BookSearch />} />
                <Route path="/books/detail" element={<BookDetail />} />
                <Route path="/shelves" element={<PrivateRoute><ShelfList /></PrivateRoute>} />
                <Route path="/shelves/:id" element={<PrivateRoute><ShelfDetail /></PrivateRoute>} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)