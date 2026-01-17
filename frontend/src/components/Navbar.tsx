import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="container flex-between h-full">
        <Link to="/" className="brand">
          <span className="text-gradient font-bold text-xl">Modu Office</span>
        </Link>

        <div className="flex-center gap-md">
          {user ? (
            <>
              <Link to="/rooms" className="nav-link">회의실</Link>
              <Link to="/my-bookings" className="nav-link">내 예약</Link>
              {user.role === 'ADMIN' && (
                <Link to="/admin" className="nav-link text-accent">관리자</Link>
              )}
              <div className="separator"></div>
              <span className="text-sm text-muted">{user.name}님</span>
              <button onClick={handleLogout} className="btn btn-secondary text-xs">로그아웃</button>
            </>
          ) : (
            <Link to="/login" className="btn btn-primary">로그인</Link>
          )}
        </div>
      </div>
    </nav>
  );
}
