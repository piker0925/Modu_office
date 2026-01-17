import { Outlet } from 'react-router-dom';
import './AuthLayout.css';

export default function AuthLayout() {
  return (
    <div className="auth-layout">
      {/* Left Side - Form Area */}
      <div className="auth-content flex-center">
        <div className="auth-container">
          <Outlet />
        </div>
      </div>

      {/* Right Side - Hero/Image Area */}
      <div className="auth-hero flex-center flex-col">
        <div className="hero-content text-center">
          <h1 className="brand-title font-bold mb-md text-white">Modu Office</h1>
          <p className="brand-subtitle text-white opacity-80">
            효율적인 공간 관리의 시작<br />
            모두의 오피스와 함께하세요
          </p>
        </div>
      </div>
    </div>
  );
}
